package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.CdiCosmos;
import it.pagopa.pagopa.apiconfig.entity.CdiDetail;
import it.pagopa.pagopa.apiconfig.entity.CdiDetailCosmos;
import it.pagopa.pagopa.apiconfig.entity.CdiFasciaCostoServizio;
import it.pagopa.pagopa.apiconfig.entity.CdiInformazioniServizio;
import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.entity.CdiPreference;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.entity.ServiceAmountCosmos;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CheckItem;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.model.psp.CdiXml;
import it.pagopa.pagopa.apiconfig.model.psp.Cdis;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiCosmosRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiFasciaCostoServizioRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiInformazioniServizioRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterValidRepository;
import it.pagopa.pagopa.apiconfig.repository.CdiPreferenceRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getExceptionErrors;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.mapXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.syntaxValidation;

@Service
@Validated
public class CdiService {

    @Autowired
    private CdiMasterRepository cdiMasterRepository;

    @Autowired
    private CdiMasterValidRepository cdiMasterValidRepository;

    @Autowired
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    @Autowired
    PspRepository pspRepository;

    @Autowired
    CanaliRepository canaliRepository;

    @Autowired
    IntermediariPspRepository intermediariPspRepository;

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

    @Autowired
    private CdiCosmosRepository cdiCosmosRepository;

    @Value("${xsd.cdi}")
    private String xsdCdi;

    public Cdis getCdis(@NotNull Integer limit, @NotNull Integer pageNumber, String idCdi, String pspCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "dataInizioValidita"));
        var filters = CommonUtil.getFilters(CdiMaster.builder()
                .idInformativaPsp(idCdi)
                .fkPsp(Psp.builder()
                        .idPsp(pspCode)
                        .build())
                .build());
        Page<CdiMaster> page = cdiMasterRepository.findAll(filters, pageable);
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

    @Transactional
    public void createCdi(MultipartFile file) {
        List<CheckItem> checks = verifyCdi(file);

        Optional<CheckItem> check = checks.stream().filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)).findFirst();
        if (check.isPresent()) {
            throw new AppException(AppError.CDI_BAD_REQUEST, String.format("[%s] %s", check.get().getValue(), check.get().getNote()));
        }

        // map file into model class
        CdiXml xml = mapXml(file, CdiXml.class);

        var psp = getPspIfExists(xml.getIdentificativoPSP());

        // save BINARY_FILE and CDI_MASTER
        var binaryFile = saveBinaryFile(file);
        var master = saveCdiMaster(xml, psp, binaryFile);
        // for each detail save DETAIL, INFORMAZIONI_SERVIZIO, FASCE_COSTO, PREFERENCES
        for (var xmlDetail : xml.getListaInformativaDetail().getInformativaDetail()) {
            var pspCanaleTipoVersamento = findPspCanaleTipoVersamentoIfExists(psp, xmlDetail);

            var detail = saveCdiDetail(master, xmlDetail, pspCanaleTipoVersamento);
            saveCdiInformazioniServizio(xmlDetail, detail);
            saveCdiFasciaCostiServizio(xmlDetail, detail);
            saveCdiPreferences(xml, xmlDetail, detail);
        }
        // save CDI to Cosmos DB
        cdiCosmosRepository.save(mapToCosmosEntity(master));
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


    public void uploadHistory() {
        var result = cdiMasterValidRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, CdiMaster.class))
                .map(this::mapToCosmosEntity)
                .collect(Collectors.toList());
        cdiCosmosRepository.saveAll(result);
    }

    private CdiCosmos mapToCosmosEntity(CdiMaster master) {
        if (master.getCdiDetail() == null) {
            throw new AppException(AppError.CDI_DETAILS_NOT_FOUND, master.getIdInformativaPsp());
        }
        var cdiDetails = master.getCdiDetail()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(this::mapDetails)
                        .collect(Collectors.toList());
        return CdiCosmos.builder()
                .idPsp(master.getFkPsp().getIdPsp())
                .idCdi(master.getIdInformativaPsp())
                .cdiStatus("NEW")
                .digitalStamp(master.getMarcaBolloDigitale())
                .validityDateFrom(master.getDataInizioValidita() != null ? master.getDataInizioValidita().toLocalDateTime() : null)
                .details(cdiDetails)
                .build();
    }

    private CdiDetailCosmos mapDetails(@NotNull CdiDetail detail) {
        @NotNull Canali canale = detail.getFkPspCanaleTipoVersamento().getCanaleTipoVersamento().getCanale();
        return CdiDetailCosmos.builder()
                .idChannel(canale.getIdCanale())
                .name(detail.getNomeServizio())
                .description(getDescription(detail))
                .channelApp(detail.getCanaleApp() == 1L)
                .paymentType(detail.getFkPspCanaleTipoVersamento().getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento())
                .idBrokerPsp(canale.getFkIntermediarioPsp().getIdIntermediarioPsp())
                .channelCardsCart(canale.getFkCanaliNodo() != null ? canale.getFkCanaliNodo().getCarrelloCarte() : null)
                .serviceAmount(detail.getCdiFasciaCostoServizio().stream()
                        .map(elem -> ServiceAmountCosmos.builder()
                                .minPaymentAmount((int) (elem.getImportoMinimo() * 100))
                                .maxPaymentAmount((int) (elem.getImportoMassimo() * 100))
                                .paymentAmount((int) (elem.getCostoFisso() * 100))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    private static String getDescription(@NotNull CdiDetail detail) {
        return detail.getCdiInformazioniServizio()
                .stream()
                .filter(item -> "IT".equals(item.getCodiceLingua()))
                .findFirst()
                .map(CdiInformazioniServizio::getDescrizioneServizio)
                .orElse("");
    }

    public List<CheckItem> verifyCdi(MultipartFile file) {
        // checks are described here https://pagopa.atlassian.net/wiki/spaces/ACN/pages/467435579/Verifica+CDI

        List<CheckItem> checkItemList = new ArrayList<>();
        // syntax checks
        String detail;
        try {
            syntaxValidation(file, xsdCdi);
            detail = "XML is valid against the XSD schema.";
            checkItemList.add(CheckItem.builder()
                    .title("XSD Schema")
                    .value(xsdCdi)
                    .valid(CheckItem.Validity.VALID)
                    .note(detail)
                    .build()
            );
        } catch (SAXException | IOException | XMLStreamException e) {
            detail = getExceptionErrors(e.getMessage());
            checkItemList.add(CheckItem.builder()
                    .title("XSD Schema")
                    .value(xsdCdi)
                    .valid(CheckItem.Validity.NOT_VALID)
                    .note(detail)
                    .build()
            );
            return checkItemList;
        }

        // map file into model class
        CdiXml xml = mapXml(file, CdiXml.class);

        // check psp
        String pspId = xml.getIdentificativoPSP();
        Psp psp = null;
        try {
            psp = getPspIfExists(pspId);
            checkItemList.addAll(checkPsp(psp, pspId, xml));

            // check marca da bollo (stamp)
            CheckItem checkItem = CommonUtil.checkData("Stamp", xml.getInformativaMaster().getMarcaBolloDigitale(), psp.getMarcaBolloDigitale(), "Stamp not consistent");
            checkItemList.add(checkItem);
        } catch (AppException e) {
            checkItemList.add(CheckItem.builder()
                    .title("PSP Identifier")
                    .value(pspId)
                    .valid(CheckItem.Validity.NOT_VALID)
                    .note("PSP identifier not consistent")
                    .build());
        }

        // check date
        checkItemList.add(CommonUtil.checkValidityDate(xml.getInformativaMaster().getDataInizioValidita()));

        // check flow id
        if (psp != null) {
            checkItemList.add(checkFlow(xml, psp));
        }

        for (CdiXml.InformativaDetail informativaDetail : xml.getListaInformativaDetail().getInformativaDetail()) {
            // check channel and paymentMethod
            String channelId = informativaDetail.getIdentificativoCanale();
            Optional<Canali> channel = canaliRepository.findByIdCanale(channelId);
            if (channel.isEmpty()) {
                checkItemList.add(CheckItem.builder()
                        .title("Channel - Payment Method")
                        .value(channelId + " - " + informativaDetail.getTipoVersamento())
                        .valid(CheckItem.Validity.NOT_VALID)
                        .note("Channel not consistent")
                        .build());
            } else if (psp != null) {
                // check payment methods
                checkItemList.add(checkPaymentMethods(channel.get(), psp, informativaDetail));

                if (informativaDetail.getModelloPagamento() == 4L) {
                    // check payment model
                    checkItemList.add(checkPaymentModel(informativaDetail));
                }

                // check broker psp
                checkItemList.add(checkBrokerPsp(channel.get(), informativaDetail));
            }

            // check amount ranges
            checkItemList.addAll(checkAmountRanges(informativaDetail));

            // check languages
            checkItemList.addAll(checkLanguages(informativaDetail));
        }

        return checkItemList;
    }

    private List<CheckItem> checkPsp(Psp psp, String pspId, CdiXml xml) {
        List<CheckItem> checkItemList = new ArrayList<>();
        checkItemList.add(CommonUtil.checkData("PSP", psp.getIdPsp(), pspId, "PSP identifier not consistent"));
        checkItemList.add(CommonUtil.checkData("Business Name", psp.getRagioneSociale(), xml.getRagioneSociale(), "Business name not consistent"));
        checkItemList.add(CommonUtil.checkData("ABI Code", psp.getAbi(), xml.getCodiceABI(), "ABI code not consistent"));
        checkItemList.add(CommonUtil.checkData("BIC Code", psp.getBic(), xml.getCodiceBIC(), "BIC code not consistent"));
        return checkItemList;
    }

    private CheckItem checkPaymentMethods(Canali channel, Psp psp, CdiXml.InformativaDetail informativaDetail) {
        List<PspCanaleTipoVersamento> paymentMethods = pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(psp.getObjId(), channel.getId());
        String paymentType = informativaDetail.getTipoVersamento() != null ? informativaDetail.getTipoVersamento() : "PO";

        CheckItem.Validity validity = paymentMethods.stream().anyMatch(pm -> pm.getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento().equals(paymentType)) ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID;
        return CheckItem.builder()
                .title("Channel - Payment Method")
                .value(channel.getIdCanale() + " - " + informativaDetail.getTipoVersamento())
                .valid(validity)
                .note(validity.equals(CheckItem.Validity.VALID) ? "" : "Channel and/or payment method not consistent")
                .build();
    }

    private List<CheckItem> checkLanguages(CdiXml.InformativaDetail informativaDetail) {
        List<String> languages = new ArrayList<>();
        List<CheckItem> checkItemList = new ArrayList<>();
        String title = "Language code";

        for (CdiXml.InformazioniServizio informazioniServizio : informativaDetail.getListaInformazioniServizio().getInformazioniServizio()) {
            languages.add(informazioniServizio.getCodiceLingua());
        }

        List<String> languagesTarget = Stream.of("IT", "EN", "DE", "FR", "SL").collect(Collectors.toList());
        languagesTarget.removeAll(languages);

        final boolean[] duplicate = {false};

        Map<String, Long> frequencyMap = languages.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        frequencyMap.entrySet().stream()
                .filter(item -> item.getValue() > 1)
                .forEach(item -> {
                            checkItemList.add(CheckItem.builder()
                                    .title(title)
                                    .value(item.getKey())
                                    .valid(CheckItem.Validity.NOT_VALID)
                                    .note(String.format("%s occurrences", item.getValue()))
                                    .build());
                            duplicate[0] = true;
                        }
                );

        if (languagesTarget.isEmpty() && !duplicate[0]) {
            checkItemList.add(CheckItem.builder()
                    .title(title)
                    .value("")
                    .valid(CheckItem.Validity.VALID)
                    .build());
        } else if (!languagesTarget.isEmpty()) {
            checkItemList.add(CheckItem.builder()
                    .title(title)
                    .value("")
                    .valid(CheckItem.Validity.NOT_VALID)
                    .note(String.format("Missing languages: %s", languagesTarget))
                    .build());
        }

        return checkItemList;
    }

    private List<CheckItem> checkAmountRanges(CdiXml.InformativaDetail informativaDetail) {
        List<Double> maxServiceAmountList = new ArrayList<>();
        List<CheckItem> checkItemList = new ArrayList<>();
        boolean valid = true;
        for (CdiXml.FasciaCostoServizio maxServiceAmount : informativaDetail.getCostiServizio().getListaFasceCostoServizio().getFasciaCostoServizio()) {
            if (maxServiceAmountList.contains(maxServiceAmount.getImportoMassimoFascia())) {
                checkItemList.add(CheckItem.builder()
                        .title("Maximum amount range")
                        .value(maxServiceAmount.getImportoMassimoFascia().toString())
                        .valid(CheckItem.Validity.NOT_VALID)
                        .note("Duplicate amount")
                        .build());
                valid = false;
            } else {
                maxServiceAmountList.add(maxServiceAmount.getImportoMassimoFascia());
            }
        }
        if (valid) {
            checkItemList.add(CheckItem.builder()
                    .title("Maximum amount ranges")
                    .value("")
                    .valid(CheckItem.Validity.VALID)
                    .build());
        }
        return checkItemList;
    }

    private CheckItem checkBrokerPsp(Canali channel, CdiXml.InformativaDetail informativaDetail) {
        String brokerPsp = informativaDetail.getIdentificativoIntermediario();
        CheckItem.Validity validity = channel.getFkIntermediarioPsp().getIdIntermediarioPsp().equals(brokerPsp) ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID;
        return CheckItem.builder()
                .title("Broker PSP")
                .value(brokerPsp)
                .valid(validity)
                .note(validity.equals(CheckItem.Validity.VALID) ? "" : "Broker Psp not related to the channel")
                .build();
    }

    private CheckItem checkPaymentModel(CdiXml.InformativaDetail informativaDetail) {
        CheckItem.Validity validity = informativaDetail.getTipoVersamento().equals("PO") ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID;
        return CheckItem.builder()
                .title("Payment model")
                .value(informativaDetail.getModelloPagamento().toString())
                .valid(validity)
                .note(validity.equals(CheckItem.Validity.VALID) ? "" : "Payment model not related to payment method")
                .build();
    }

    private CheckItem checkFlow(CdiXml xml, Psp psp) {
        Optional<CdiMaster> optFlow = cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(xml.getIdentificativoFlusso(), psp.getIdPsp());
        return CheckItem.builder()
                .title("Flow identifier")
                .value(xml.getIdentificativoFlusso())
                .valid(optFlow.isPresent() ? CheckItem.Validity.NOT_VALID : CheckItem.Validity.VALID)
                .note(optFlow.isPresent() ? "Flow identifier already exists" : "")
                .build();
    }

    /**
     * save CdiPreference in DB
     *
     * @param xml       CDI file
     * @param xmlDetail informativaDetail: detail element to save
     * @param detail    FK CdiDetail
     */
    private void saveCdiPreferences(@NotNull CdiXml xml, @NotNull CdiXml.InformativaDetail xmlDetail, @NotNull CdiDetail detail) {
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
    private void saveCdiFasciaCostiServizio(@NotNull CdiXml.InformativaDetail detail, @NotNull CdiDetail detailEntity) {
        CdiXml.CostiServizio costiServizio = detail.getCostiServizio();
        if (costiServizio != null && costiServizio.getListaFasceCostoServizio() != null && costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio() != null) {
            // sort by importoMassimo and create fascia costi servizio
            var importi = costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().stream()
                    .map(CdiXml.FasciaCostoServizio::getImportoMassimoFascia)
                    .sorted(Double::compareTo)
                    .distinct()
                    .collect(Collectors.toList());
            // for each fascia save an element in the database
            for (int i = 0; i < costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().size(); i++) {
                var fascia = costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().get(i);
                // importoMinimo is equals to previous importoMassimo (equals to 0 for the first element)
                var prev = importi.stream()
                        .filter(elem -> elem < fascia.getImportoMassimoFascia())
                        .max(Double::compareTo)
                        .orElse(0.0);
                CdiXml.ConvenzioniCosti convenzione = null;
                if (fascia.getListaConvenzioniCosti() != null) {
                    convenzione = fascia.getListaConvenzioniCosti()
                            .stream()
                            .findFirst()
                            .orElse(null);
                }
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

    /**
     * @param detail       info detail in the XML
     * @param detailEntity FK CdiDetail
     */
    private void saveCdiInformazioniServizio(@NotNull CdiXml.InformativaDetail detail, @NotNull CdiDetail detailEntity) {
        if (detail.getListaInformazioniServizio() != null && detail.getListaInformazioniServizio().getInformazioniServizio() != null) {
            for (var info : detail.getListaInformazioniServizio().getInformazioniServizio()) {
                cdiInformazioniServizioRepository.save(CdiInformazioniServizio.builder()
                        .codiceLingua(info.getCodiceLingua() != null ? info.getCodiceLingua() : "IT")
                        .descrizioneServizio(info.getDescrizioneServizio() != null ? info.getDescrizioneServizio() : "Pagamento con CBILL")
                        .disponibilitaServizio(info.getDisponibilitaServizio() != null ? info.getDisponibilitaServizio() : "24/7/7")
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
    private CdiDetail saveCdiDetail(@NotNull CdiMaster master, @NotNull CdiXml.InformativaDetail detail, @NotNull PspCanaleTipoVersamento pspCanaleTipoVersamento) {
        CdiDetail.CdiDetailBuilder builder = CdiDetail.builder()
                .priorita(detail.getPriorita() != null ? detail.getPriorita() : 0L)
                .modelloPagamento(detail.getModelloPagamento() != null ? detail.getModelloPagamento() : 4L)
                .fkCdiMaster(master)
                .fkPspCanaleTipoVersamento(pspCanaleTipoVersamento)
                .canaleApp(detail.getCanaleApp() != null ? detail.getCanaleApp() : 0L);
        var identificazioneServizio = detail.getIdentificazioneServizio();
        if (identificazioneServizio != null) {
            builder.nomeServizio(identificazioneServizio.getNomeServizio())
                    .logoServizio(identificazioneServizio.getLogoServizio() != null ? identificazioneServizio.getLogoServizio().strip().getBytes() : null);
        }
        if (detail.getListaParoleChiave() != null) {
            // join list of ParolaChiave in a string semicolon separated ([tag1, tag2,tag3] -> "tag1;tag2;tag3")
            String tags = detail.getListaParoleChiave().stream()
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
    private PspCanaleTipoVersamento findPspCanaleTipoVersamentoIfExists(@NotNull Psp psp, @NotNull CdiXml.InformativaDetail detail) {
        String paymentType = detail.getTipoVersamento() != null ? detail.getTipoVersamento() : "PO";
        return pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(psp.getObjId(),
                        detail.getIdentificativoCanale(),
                        paymentType)
                .orElseThrow(() -> new AppException(AppError.CDI_BAD_REQUEST, "tipoVersamento: " + paymentType + " not found"));
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
                .dataPubblicazione(Timestamp.valueOf(xml.getInformativaMaster().getDataPubblicazione()))
                .dataInizioValidita(Timestamp.valueOf(xml.getInformativaMaster().getDataInizioValidita()))
                .idInformativaPsp(xml.getIdentificativoFlusso())
                .logoPsp(xml.getInformativaMaster().getLogoPSP().strip().getBytes())
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
