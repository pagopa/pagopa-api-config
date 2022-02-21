package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.InformativePaDetail;
import it.pagopa.pagopa.apiconfig.entity.InformativePaFasce;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartXml;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativePaDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativePaFasceRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativePaMasterRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.mapXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.syntacticValidationXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toTimestamp;

@Service
@Validated
public class CounterpartService {

    @Autowired
    InformativePaMasterRepository informativePaMasterRepository;

    @Autowired
    InformativePaDetailRepository informativePaDetailRepository;

    @Autowired
    InformativePaFasceRepository informativePaFasceRepository;

    @Autowired
    BinaryFileRepository binaryFileRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private PaRepository paRepository;

    @Value("${xsd.counterpart}")
    private String xsdCounterpart;

    public CounterpartTables getCounterpartTables(@NotNull Integer limit, @NotNull Integer pageNumber, String idCounterpartTable, String creditorInstitutionCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        var filters = CommonUtil.getFilters(InformativePaMaster.builder()
                .idInformativaPa(idCounterpartTable)
                .fkPa(Pa.builder()
                        .idDominio(creditorInstitutionCode)
                        .build())
                .build());
        Page<InformativePaMaster> page = informativePaMasterRepository.findAll(filters, pageable);
        return CounterpartTables.builder()
                .counterpartTableList(getCounterpartList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public byte[] getCounterpartTable(@NotNull String idCounterpartTable, @NotNull String creditorInstitutionCode) {
        var result = getInformativePaMasterIfExists(idCounterpartTable, creditorInstitutionCode);
        return result.getFkBinaryFile().getFileContent();
    }

    public void createCounterpartTable(MultipartFile file) {
        // syntactic checks
        try {
            syntacticValidationXml(file, xsdCounterpart);
        } catch (SAXException | IOException | XMLStreamException e) {
            throw new AppException(AppError.COUNTERPART_BAD_REQUEST, e, e.getMessage());
        }

        // map file into model class
        CounterpartXml counterpartXml = mapXml(file, CounterpartXml.class);

        // semantics checks
        var pa = getPaIfExists(counterpartXml.getIdentificativoDominio());
        checkFlusso(counterpartXml, pa);
        checkRagioneSociale(counterpartXml, pa);
        checkValidityDate(counterpartXml);

        // save
        var binaryFile = saveBinaryFile(file);
        var infoMaster = informativePaMasterRepository.save(InformativePaMaster.builder()
                .idInformativaPa(counterpartXml.getIdentificativoFlusso())
                .dataPubblicazione(toTimestamp(counterpartXml.getDataPubblicazione()))
                .dataInizioValidita(toTimestamp(counterpartXml.getDataInizioValidita()))
                .fkBinaryFile(binaryFile)
                .pagamentiPressoPsp(counterpartXml.getPagamentiPressoPSP())
                .fkPa(pa)
                .build());
        saveDetail(counterpartXml, infoMaster);
    }


    public void deleteCounterpartTable(@NotBlank String idCounterpartTable, @NotBlank String creditorInstitutionCode) {
        InformativePaMaster master = getInformativePaMasterIfExists(idCounterpartTable, creditorInstitutionCode);
        InformativePaMaster valid = getValidCounterpart(creditorInstitutionCode);
        if (valid != null && valid.getId().equals(master.getId())) {
            throw new AppException(HttpStatus.CONFLICT, "Counterpart conflict", "This Counterpart is used.");
        }
        informativePaMasterRepository.delete(master);
    }

    /**
     * @param creditorInstitutionCode ID dominio
     * @return the valid configuration in the DB. Null if not valid configuration is found
     */
    private InformativePaMaster getValidCounterpart(String creditorInstitutionCode) {
        return informativePaMasterRepository.findByFkPa_IdDominioAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(creditorInstitutionCode, Timestamp.valueOf(LocalDateTime.now()))
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * save in the DB InformativePaMaster
     *
     * @param counterpartXml file xml
     * @param infoMaster     info {@link InformativePaMaster}
     */
    private void saveDetail(CounterpartXml counterpartXml, InformativePaMaster infoMaster) {
        counterpartXml.getErogazioneServizio().getDisponibilita().forEach(elem -> {
            var detail = informativePaDetailRepository.save(InformativePaDetail.builder()
                    .fkInformativaPaMaster(infoMaster)
                    .tipo(elem.getTipoPeriodo())
                    .giorno(elem.getGiorno())
                    .flagDisponibilita(true)
                    .build());
            informativePaFasceRepository.save(InformativePaFasce.builder()
                    .fkInformativaPaDetail(detail)
                    .oraA(elem.getFasciaOraria().getFasciaOrariaA().toString())
                    .oraDa(elem.getFasciaOraria().getFasciaOrariaDa().toString())
                    .build());

        });
        counterpartXml.getErogazioneServizio().getIndisponibilita().forEach(elem -> {
            var detail = informativePaDetailRepository.save(InformativePaDetail.builder()
                    .fkInformativaPaMaster(infoMaster)
                    .tipo(elem.getTipoPeriodo())
                    .giorno(elem.getGiorno())
                    .flagDisponibilita(false)
                    .build());
            informativePaFasceRepository.save(InformativePaFasce.builder()
                    .fkInformativaPaDetail(detail)
                    .oraA(elem.getFasciaOraria().getFasciaOrariaA().toString())
                    .oraDa(elem.getFasciaOraria().getFasciaOrariaDa().toString())
                    .build());
        });
    }


    /**
     * @param idCounterpartTable      ID of a counterpart table
     * @param creditorInstitutionCode Creditor institution code
     * @return search on DB and return the {@link InformativePaMaster} if it is present
     * @throws AppException if not found
     */
    private InformativePaMaster getInformativePaMasterIfExists(String idCounterpartTable, String creditorInstitutionCode) {
        return informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(idCounterpartTable, creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.COUNTERPART_NOT_FOUND, idCounterpartTable, creditorInstitutionCode));
    }

    /**
     * Maps InformativePaMaster objects stored in the DB in a List of CounterpartTable
     *
     * @param page page of {@link InformativePaMaster} returned from the database
     * @return a list of {@link CounterpartTable}.
     */
    private List<CounterpartTable> getCounterpartList(Page<InformativePaMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, CounterpartTable.class))
                .collect(Collectors.toList());
    }

    /**
     * @param creditorInstitutionCode identificativo Dominio
     * @return get the PA from DB using identificativoDominio
     */
    private Pa getPaIfExists(String creditorInstitutionCode) {
        return paRepository.findByIdDominio(creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.ICA_BAD_REQUEST, creditorInstitutionCode + " not found"));
    }

    /**
     * check if ragioneSociale in the xml is right
     *
     * @param xml XML file
     * @param pa  the PA from DB
     */
    private void checkRagioneSociale(CounterpartXml xml, Pa pa) {
        if (!pa.getRagioneSociale().equals(xml.getRagioneSociale())) {
            throw new AppException(AppError.COUNTERPART_BAD_REQUEST, "There is an error in '" + xml.getRagioneSociale() + "'");
        }
    }

    /**
     * @param xml check if the validity is after today
     */
    private void checkValidityDate(CounterpartXml xml) {
        var now = LocalDate.now();
        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59));
        if (toTimestamp(xml.getDataInizioValidita()).before(tomorrow)) {
            throw new AppException(AppError.ICA_BAD_REQUEST, "Validity start date must be greater than the today's date");
        }
    }

    /**
     * check if flusso in the xml already exists
     *
     * @param xml XML file
     * @param pa  the PA from DB
     */
    private void checkFlusso(CounterpartXml xml, Pa pa) {
        if (informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(xml.getIdentificativoFlusso(), pa.getIdDominio()).isPresent()) {
            throw new AppException(AppError.COUNTERPART_CONFLICT, xml.getIdentificativoFlusso());
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
