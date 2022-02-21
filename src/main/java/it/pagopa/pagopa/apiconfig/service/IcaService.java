package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoDetail;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.IcaXml;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.XSDValidation;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getAbiFromIban;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getCcFromIban;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.mapXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.syntacticValidationXml;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toTimestamp;

@Service
@Validated
public class IcaService {

    public static final String ABI_IBAN_POSTALI = "07601";

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

    @Autowired
    private BinaryFileRepository binaryFileRepository;

    @Autowired
    private InformativeContoAccreditoDetailRepository informativeContoAccreditoDetailRepository;

    @Autowired
    private CodifichePaRepository codifichePaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${xsd.ica}")
    private String xsdIca;

    public Icas getIcas(@NotNull Integer limit, @NotNull Integer pageNumber, String idIca, String creditorInstitutionCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        var filters = CommonUtil.getFilters(InformativeContoAccreditoMaster.builder()
                .idInformativaContoAccreditoPa(idIca)
                .fkPa(Pa.builder()
                        .idDominio(creditorInstitutionCode)
                        .build())
                .build());
        Page<InformativeContoAccreditoMaster> page = informativeContoAccreditoMasterRepository.findAll(filters, pageable);
        return Icas.builder()
                .icaList(getIcaList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public byte[] getIca(@NotNull String idIca, String creditorInstitutionCode) {
        var result = getIcaMasterIfExists(idIca, creditorInstitutionCode);
        return result.getFkBinaryFile().getFileContent();
    }

    public XSDValidation verifyXSD(MultipartFile xml) {
        boolean xsdEvaluated = false;
        String lineNumber = "";
        String detail;

        try {
            syntacticValidationXml(xml, xsdIca);
            xsdEvaluated = true;
            detail = "XML is valid against the XSD schema.";
        } catch (SAXException | IOException | XMLStreamException e) {
            String stringException = e.getMessage();
            Matcher matcher = Pattern.compile("lineNumber: [0-9]*").matcher(stringException);
            if (matcher.find()) {
                lineNumber = matcher.group(0);
            }
            detail = stringException.substring(stringException.lastIndexOf(":") + 1).trim();
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(detail);
        if (lineNumber.length() > 0) {
            stringBuilder.append(" Error at ").append(lineNumber);
        }

        XSDValidation response = new XSDValidation();
        response.setXsdCompliant(xsdEvaluated);
        response.setXsdSchema(xsdIca);
        response.setDetail(stringBuilder.toString());

        return response;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createIca(@NotNull MultipartFile file) {
        // syntactic checks
        checkSyntax(file);

        // map file into model class
        IcaXml icaXml = mapXml(file, IcaXml.class);

        // semantics checks
        var pa = getPaIfExists(icaXml.getIdentificativoDominio());
        checkFlusso(icaXml, pa);
        checkRagioneSociale(icaXml, pa);
        checkQrCode(pa);
        checkValidityDate(icaXml);
        checkPostalIban(icaXml, pa);

        // save
        var binaryFile = saveBinaryFile(file);
        var icaMaster = saveIcaMaster(icaXml, pa, binaryFile);
        for (Object elem : icaXml.getContiDiAccredito()) {
            saveIcaDetail(elem, icaMaster);
        }
    }

    public void deleteIca(String idIca, String creditorInstitutionCode) {
        var icaMaster = getIcaMasterIfExists(idIca, creditorInstitutionCode);
        var valid = informativeContoAccreditoMasterRepository.findByFkPa_IdDominioAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(creditorInstitutionCode, Timestamp.valueOf(LocalDateTime.now()));
        if (!valid.isEmpty() && valid.get(0).getId().equals(icaMaster.getId())) {
            throw new AppException(HttpStatus.CONFLICT, "ICA conflict", "This ICA is used.");
        }
        informativeContoAccreditoMasterRepository.delete(icaMaster);
    }

    /**
     * @param idIca                   id ICA
     * @param creditorInstitutionCode id dominio
     * @return return the entity from the DB if exists
     */
    private InformativeContoAccreditoMaster getIcaMasterIfExists(String idIca, String creditorInstitutionCode) {
        return informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(idIca, creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.ICA_NOT_FOUND, idIca));
    }

    /**
     * check if flusso in the xml already exists
     *
     * @param icaXml XML file
     * @param pa     the PA from DB
     */
    private void checkFlusso(IcaXml icaXml, Pa pa) {
        if (informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(icaXml.getIdentificativoFlusso(), pa.getIdDominio()).isPresent()) {
            throw new AppException(AppError.ICA_CONFLICT, icaXml.getIdentificativoFlusso());
        }
    }

    /**
     * for each postal iban check if it is associated with a BARCODE-128-AIM encoding
     * NOTE: is postal iban if ABI in the IBAN is equals to 07601.
     *
     * @param icaXml file with ibans
     * @param pa     the entity from DB
     */
    private void checkPostalIban(IcaXml icaXml, Pa pa) {
        icaXml.getContiDiAccredito().forEach(
                elem -> {
                    String iban = getIbanFromContoAccredito(elem);
                    if (ABI_IBAN_POSTALI.equals(getAbiFromIban(iban))
                            && hasBarcodeEncodings(pa, iban)) {
                        throw new AppException(AppError.ICA_BAD_REQUEST, "BARCODE-128-AIM encoding missing for " + iban);
                    }
                }
        );
    }

    /**
     * @param pa   PA of the IBAN
     * @param iban IBAN to check
     * @return true if the IBAN of the PA has BARCODE-128-AIM encoding
     */
    private boolean hasBarcodeEncodings(Pa pa, String iban) {
        return codifichePaRepository.findByCodicePaAndFkPa_ObjId(getCcFromIban(iban), pa.getObjId()).stream()
                .noneMatch(i -> i.getFkCodifica().getIdCodifica().equals("BARCODE-128-AIM"));
    }

    /**
     * @param elem String or {@link IcaXml.InfoContoDiAccreditoPair}
     * @return the iban if valid
     */
    private String getIbanFromContoAccredito(Object elem) {
        String iban = null;
        if (elem instanceof String) {
            iban = String.valueOf(elem);
        } else if (elem instanceof IcaXml.InfoContoDiAccreditoPair) {
            iban = ((IcaXml.InfoContoDiAccreditoPair) elem).getIbanAccredito();
        }
        if (iban == null || iban.length() != 27) {
            throw new AppException(AppError.ICA_BAD_REQUEST, iban + " is not a valid iban");
        }
        return iban;
    }

    /**
     * @param icaXml check if the validity is after today
     */
    private void checkValidityDate(IcaXml icaXml) {
        var now = LocalDate.now();
        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59));
        if (toTimestamp(icaXml.getDataInizioValidita()).before(tomorrow)) {
            throw new AppException(AppError.ICA_BAD_REQUEST, "Validity start date must be greater than the today's date");
        }
    }

    /**
     * @param pa check if PA has QR-CODE encodings
     */
    private void checkQrCode(Pa pa) {
        boolean hasQrcodeEncoding = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId())
                .stream()
                .noneMatch(elem -> elem.getFkCodifica().getIdCodifica().equals("QR-CODE"));
        if (hasQrcodeEncoding) {
            throw new AppException(AppError.ICA_BAD_REQUEST, "QR-CODE encoding missing for " + pa.getIdDominio());
        }
    }

    /**
     * check if ragioneSociale in the xml is right
     *
     * @param icaXml XML file
     * @param pa     the PA from DB
     */
    private void checkRagioneSociale(IcaXml icaXml, Pa pa) {
        if (!pa.getRagioneSociale().equals(icaXml.getRagioneSociale())) {
            throw new AppException(AppError.ICA_BAD_REQUEST, "There is an error in '" + icaXml.getRagioneSociale() + "'");
        }
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
     * @param contoAccredito info icaDetail
     * @param icaMaster      used for FK
     */
    private void saveIcaDetail(Object contoAccredito, InformativeContoAccreditoMaster icaMaster) {
        String iban = null;
        String bank = null;
        if (contoAccredito instanceof String) {
            iban = String.valueOf(contoAccredito);
        } else if (contoAccredito instanceof IcaXml.InfoContoDiAccreditoPair) {
            iban = ((IcaXml.InfoContoDiAccreditoPair) contoAccredito).getIbanAccredito();
            bank = ((IcaXml.InfoContoDiAccreditoPair) contoAccredito).getIdBancaSeller();
        }

        var icaDetail = InformativeContoAccreditoDetail.builder()
                .ibanAccredito(iban)
                .idBancaSeller(bank)
                .fkInformativaContoAccreditoMaster(icaMaster)
                .build();
        informativeContoAccreditoDetailRepository.save(icaDetail);
    }

    /**
     * @param icaXml     info icaMaster
     * @param pa         used for FK
     * @param binaryFile used for FK
     * @return the entity saved in the database
     */
    private InformativeContoAccreditoMaster saveIcaMaster(IcaXml icaXml, Pa pa, BinaryFile binaryFile) {
        var icaMaster = InformativeContoAccreditoMaster.builder()
                .dataInizioValidita(toTimestamp(icaXml.getDataInizioValidita()))
                .dataPubblicazione(toTimestamp(icaXml.getDataPubblicazione()))
                .ragioneSociale(icaXml.getRagioneSociale())
                .idInformativaContoAccreditoPa(icaXml.getIdentificativoFlusso())
                .fkBinaryFile(binaryFile)
                .fkPa(pa)
                .build();

        return informativeContoAccreditoMasterRepository.save(icaMaster);
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


    /**
     * Maps InformativeContoAccreditoMaster objects stored in the DB in a List of Ica
     *
     * @param page page of {@link InformativeContoAccreditoMaster} returned from the database
     * @return a list of {@link Ica}.
     */
    private List<Ica> getIcaList(Page<InformativeContoAccreditoMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Ica.class))
                .collect(Collectors.toList());
    }

    /**
     * check syntactic and XSD of a ICA file
     *
     * @param file ICA XML to check
     */
    private void checkSyntax(MultipartFile file) {
        try {
            syntacticValidationXml(file, xsdIca);
        } catch (SAXException | IOException | XMLStreamException e) {
            throw new AppException(AppError.ICA_BAD_REQUEST, e, e.getMessage());
        }
    }
}
