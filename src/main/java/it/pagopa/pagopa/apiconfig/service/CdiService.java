package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.CdiDetail;
import it.pagopa.pagopa.apiconfig.entity.CdiFasciaCostoServizio;
import it.pagopa.pagopa.apiconfig.entity.CdiInformazioniServizio;
import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.entity.CdiPreference;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CdiXml;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.model.psp.Cdis;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiFasciaCostoServizioRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiInformazioniServizioRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiPreferenceRepository;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.mapXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.syntacticValidationXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toTimestamp;

@Service
@Validated
public class CdiService {

    @Autowired
    private CdiMasterRepository cdiMasterRepository;

    @Autowired
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    @Autowired
    PspRepository pspRepository;

    @Autowired
    BinaryFileRepository binaryFileRepository;

    @Autowired
    private CdiDetailRepository cdiDetailRepository;

    @Autowired
    private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;

    @Autowired
    private CdiFasciaCostoServizioRepository cdiFasciaCostoServizioRepository;

    @Autowired
    CdiPreferenceRepository cdiPreferenceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${xsd.cdi}")
    private String xsdCdi;

    public Cdis getCdis(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<CdiMaster> page = cdiMasterRepository.findAll(pageable);
        return Cdis.builder()
                .cdiList(getCdiList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public byte[] getCdi(@NotBlank String idCdi, @NotBlank String pspCode) {
        CdiMaster cdiMaster = cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(idCdi, pspCode)
                .orElseThrow(() -> new AppException(AppError.CDI_NOT_FOUND, idCdi));

        return cdiMaster.getFkBinaryFile().getFileContent();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createCdi(MultipartFile file) {
        // syntactic checks
        try {
            syntacticValidationXml(file, xsdCdi);
        } catch (SAXException | IOException | XMLStreamException e) {
            throw new AppException(AppError.COUNTERPART_BAD_REQUEST, e, e.getMessage());
        }

        // map file into model class
        CdiXml xml = mapXml(file, CdiXml.class);

        // semantics checks
        var psp = getPspIfExists(xml.getIdentificativoPSP());
        checkFlusso(xml, psp);
        checkRagioneSociale(xml, psp);
        checkValidityDate(xml);

        // save BINARY_FILE and CDI_MASTER
        var binaryFile = saveBinaryFile(file);
        var master = saveCdiMaster(xml, psp, binaryFile);
        // for each detail save DETAIL, INFORMAZIONI_SERVIZIO, FASCIE_COSTO, PREFERENCES
        for (var xmlDetail : xml.getListaInformativaDetail()) {
            var pspCanaleTipoVersamento = findPspCanaleTipoVersamentoIfExists(psp, xmlDetail);

            var detail = saveCdiDetail(master, xmlDetail, pspCanaleTipoVersamento);
            saveCdiInformazioniServizio(xmlDetail, detail);
            saveCdiFasciaCostiServizio(xmlDetail, detail);
            saveCdiPreferences(xml, xmlDetail, detail);
        }


    }

    public void deleteCdi(String idCdi, String pspCode) {
        CdiMaster cdiMaster = cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(idCdi, pspCode)
                .orElseThrow(() -> new AppException(AppError.CDI_NOT_FOUND, idCdi));

        var valid = cdiMasterRepository.findByFkPsp_IdPspAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(pspCode, Timestamp.valueOf(LocalDateTime.now()));
        if (!valid.isEmpty() && valid.get(0).getId().equals(cdiMaster.getId())) {
            throw new AppException(HttpStatus.CONFLICT, "CDI conflict", "This CDI is used.");
        }
        cdiMasterRepository.delete(cdiMaster);
    }

    /**
     * save CdiPreference in DB
     *
     * @param xml       CDI file
     * @param xmlDetail informativaDetail: detail element to save
     * @param detail    FK CdiDetail
     */
    private void saveCdiPreferences(CdiXml xml, CdiXml.InformativaDetail xmlDetail, CdiDetail detail) {
        if (xmlDetail.getListaConvenzioni() != null) {
            xmlDetail.getListaConvenzioni().forEach(elem ->
            {
                double costoConvenzione = xmlDetail.getCostiServizio() != null && xmlDetail.getCostiServizio().getCostoConvenzione() != null
                        ? xmlDetail.getCostiServizio().getCostoConvenzione()
                        : 0;
                cdiPreferenceRepository.save(CdiPreference.builder()
                        .cdiDetail(detail)
                        .seller(xml.getMybankIDVS())
                        .buyer(elem.getCodiceConvenzione())
                        .costoConvenzione(costoConvenzione)
                        .build());
            });
        }
    }

    /**
     * @param detail       info detail in the XML
     * @param detailEntity FK CdiDetail
     */
    private void saveCdiFasciaCostiServizio(CdiXml.InformativaDetail detail, CdiDetail detailEntity) {
        CdiXml.CostiServizio costiServizio = detail.getCostiServizio();
        if (costiServizio != null && costiServizio.getListaFasceCostoServizio() != null) {
            // sort by importoMassimo
            costiServizio.getListaFasceCostoServizio().sort(Comparator.comparing(CdiXml.FasciaCostoServizio::getImportoMassimoFascia));
            // for each fascia save an element in the database
            for (int i = 0; i < costiServizio.getListaFasceCostoServizio().size(); i++) {
                var fascia = costiServizio.getListaFasceCostoServizio().get(i);
                // importoMinimo is equals to importoMassimo of previous element, sorted by importoMassimo (equals to 0 for the first element)
                var prev = i > 0 ? costiServizio.getListaFasceCostoServizio().get(i - 1).getImportoMassimoFascia() : 0;
                if (fascia.getListaConvenzioniCosti() != null) {
                    var convenzione = fascia.getListaConvenzioniCosti()
                            .stream()
                            .findFirst()// TODO handle list with size > 1 ?
                            .orElse(null);

                    cdiFasciaCostoServizioRepository.save(CdiFasciaCostoServizio.builder()
                            .importoMassimo(fascia.getImportoMassimoFascia())
                            .importoMinimo(prev)
                            .costoFisso(fascia.getCostoFisso())
                            .fkCdiDetail(detailEntity)
                            .valoreCommissione(fascia.getValoreCommissione())
                            .codiceConvenzione(convenzione != null ? convenzione.getCodiceConvenzione() : null)
                            .build());
                }
            }
        }

    }

    /**
     * @param detail       info detail in the XML
     * @param detailEntity FK CdiDetail
     */
    private void saveCdiInformazioniServizio(CdiXml.InformativaDetail detail, CdiDetail detailEntity) {
        if (detail.getListaInformazioniServizio() != null) {
            for (var info : detail.getListaInformazioniServizio()) {
                cdiInformazioniServizioRepository.save(CdiInformazioniServizio.builder()
                        .codiceLingua(info.getCodiceLingua())
                        .descrizioneServizio(info.getDescrizioneServizio())
                        .disponibilitaServizio(info.getDisponibilitaServizio())
                        .urlInformazioniCanale(info.getUrlInformazioniCanale())
                        .fkCdiDetail(detailEntity)
                        .limitazioniServizio(info.getLimitazioniServizio())
                        .build());
            }
        }

    }

    /**
     * @param master                  CdiMaster entity
     * @param detail                  Info Detail of XML
     * @param pspCanaleTipoVersamento FK PspCanaleTipoVersamento
     * @return save and return a CdiDetail entity
     */
    private CdiDetail saveCdiDetail(CdiMaster master, CdiXml.InformativaDetail detail, PspCanaleTipoVersamento pspCanaleTipoVersamento) {
        CdiDetail.CdiDetailBuilder builder = CdiDetail.builder()
                .priorita(detail.getPriorita())
                .modelloPagamento(detail.getModelloPagamento())
                .fkCdiMaster(master)
                .fkPspCanaleTipoVersamento(pspCanaleTipoVersamento)
                .canaleApp(detail.getCanaleApp());
        if (detail.getIdentificazioneServizio() != null) {
            var identificazioneServizio = detail.getIdentificazioneServizio();
            builder.nomeServizio(identificazioneServizio.getNomeServizio())
                    .logoServizio(identificazioneServizio.getLogoServizio() != null ? identificazioneServizio.getLogoServizio().getBytes() : null);
        }
        if (detail.getListaParoleChiave() != null) {
            // join list of ParolaChiave in a string semicolon separated ([tag1, tag2,tag3] -> "tag1;tag2;tag3")
            String tags = detail.getListaParoleChiave().stream()
                    .filter(Objects::nonNull)
                    .map(CdiXml.ParolaChiave::getParoleChiave)
                    .filter(Objects::nonNull)
                    .reduce((a, b) -> a + ";" + b)
                    .orElse(null);
            builder.tags(tags);
        }
        return cdiDetailRepository.save(builder.build());
    }

    /**
     * @param psp    entity PSP
     * @param detail InformativaDetail
     * @return save in DB and return a PspCanaleTipoVersamento
     */
    private PspCanaleTipoVersamento findPspCanaleTipoVersamentoIfExists(Psp psp, CdiXml.InformativaDetail detail) {
        return pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(psp.getObjId(),
                        detail.getIdentificativoCanale(),
                        detail.getTipoVersamento())
                .orElseThrow(() -> new AppException(AppError.CDI_BAD_REQUEST, detail.getTipoVersamento() + " not found"));
    }

    /**
     * save CdiMaster in DB and return the entity
     *
     * @param xml        CDI file
     * @param psp        FK PSP
     * @param binaryFile FK BinaryFile
     * @return entity saved in DB
     */
    private CdiMaster saveCdiMaster(CdiXml xml, Psp psp, BinaryFile binaryFile) {
        return cdiMasterRepository.save(CdiMaster.builder()
                .dataPubblicazione(toTimestamp(xml.getInformativaMaster().getDataPubblicazione()))
                .dataInizioValidita(toTimestamp(xml.getInformativaMaster().getDataInizioValidita()))
                .idInformativaPsp(xml.getIdentificativoFlusso())
                .logoPsp(xml.getInformativaMaster().getLogoPSP().getBytes())
                .urlInformazioniPsp(xml.getInformativaMaster().getUrlInformazioniPSP())
                .marcaBolloDigitale(xml.getInformativaMaster().getMarcaBolloDigitale())
                .stornoPagamento(xml.getInformativaMaster().getStornoPagamento())
                .fkPsp(psp)
                .fkBinaryFile(binaryFile)
                .build());
    }

    /**
     * Maps CdiMaster objects stored in the DB in a List of Cdi
     *
     * @param page page of {@link CdiMaster} returned from the database
     * @return a list of {@link Cdi}.
     */
    private List<Cdi> getCdiList(Page<CdiMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Cdi.class))
                .collect(Collectors.toList());
    }

    /**
     * @param pspCode Code of the payment service provider
     * @return the PSP record from DB if Exists
     */
    private Psp getPspIfExists(String pspCode) {
        return pspRepository.findByIdPsp(pspCode)
                .orElseThrow(() -> new AppException(AppError.PSP_NOT_FOUND, pspCode));
    }

    /**
     * check if flusso in the xml already exists
     *
     * @param xml XML file
     * @param psp the PSP from DB
     */
    private void checkFlusso(CdiXml xml, Psp psp) {
        if (cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(xml.getIdentificativoFlusso(), psp.getIdPsp()).isPresent()) {
            throw new AppException(AppError.CDI_CONFLICT, xml.getIdentificativoFlusso());
        }
    }

    /**
     * check if ragioneSociale in the xml is right
     *
     * @param xml XML file
     * @param psp the PSP from DB
     */
    private void checkRagioneSociale(CdiXml xml, Psp psp) {
        if (!psp.getRagioneSociale().equals(xml.getRagioneSociale())) {
            throw new AppException(AppError.CDI_CONFLICT, "There is an error in '" + xml.getRagioneSociale() + "'");
        }
    }

    /**
     * @param xml check if the validity is after today
     */
    private void checkValidityDate(CdiXml xml) {
        var now = LocalDate.now();
        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59));
        if (toTimestamp(xml.getInformativaMaster().getDataInizioValidita()).before(tomorrow)) {
            throw new AppException(AppError.ICA_BAD_REQUEST, "Validity start date must be greater than the today's date");
        }
    }

    /**
     * @param file binaryFile to save
     * @return the entity saved in the database
     */
    private BinaryFile saveBinaryFile(MultipartFile file) {
        BinaryFile binaryFile;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(file.getBytes());
            binaryFile = BinaryFile.builder()
                    .fileContent(file.getBytes())
                    .fileSize(file.getSize())
                    .fileHash(md.digest())
                    .build();
        } catch (Exception e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
        return binaryFileRepository.save(binaryFile);
    }
}
