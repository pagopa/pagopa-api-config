package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.*;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CheckItem;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.*;
import it.pagopa.pagopa.apiconfig.repository.*;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.apache.commons.validator.routines.IBANValidator;
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

import javax.validation.constraints.NotNull;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.*;

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
    private IbanValidiPerPaRepository ibanValidiPerPaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${xsd.ica}")
    private String xsdIca;

    public Icas getIcas(@NotNull Integer limit, @NotNull Integer pageNumber, String idIca, String creditorInstitutionCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "dataInizioValidita"));
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
            syntaxValidation(xml, xsdIca);
            xsdEvaluated = true;
            detail = "XML is valid against the XSD schema.";
        } catch (SAXException | IOException | XMLStreamException e) {
            String stringException = e.getMessage();
            Matcher matcher = Pattern.compile("lineNumber: \\d*").matcher(stringException);
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

    @Transactional
    public void createIca(@NotNull MultipartFile file) {
        // TODO
//        // syntactic checks
//        checkSyntax(file);
//
//        // map file into model class
//        IcaXml icaXml = mapXml(file, IcaXml.class);
//
//        // semantics checks
//        var pa = getPaIfExists(icaXml.getIdentificativoDominio());
//        checkFlusso(icaXml, pa);
//        checkRagioneSociale(icaXml, pa);
//        checkQrCode(pa);
//        checkValidityDate(icaXml);
//        checkPostalIban(icaXml, pa);
//
//        // save
//        var binaryFile = saveBinaryFile(file);
//        var icaMaster = saveIcaMaster(icaXml, pa, binaryFile);
//        for (Object elem : icaXml.getContiDiAccredito()) {
//            saveIcaDetail(elem, icaMaster);
//        }
    }

    public void deleteIca(String idIca, String creditorInstitutionCode) {
        var icaMaster = getIcaMasterIfExists(idIca, creditorInstitutionCode);
        var valid = informativeContoAccreditoMasterRepository.findByFkPa_IdDominioAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(creditorInstitutionCode, Timestamp.valueOf(LocalDateTime.now()));
        if (!valid.isEmpty() && valid.get(0).getId().equals(icaMaster.getId())) {
            throw new AppException(HttpStatus.CONFLICT, "ICA conflict", "This ICA is used.");
        }
        informativeContoAccreditoMasterRepository.delete(icaMaster);
    }

    public List<CheckItem> verifyIca(MultipartFile file) {
        // checks are described here https://pagopa.atlassian.net/wiki/spaces/ACN/pages/441517396/Verifica+ICA
        List<CheckItem> checkItemList = new ArrayList<>();

        // syntax checks
        String detail;
        try {
            syntaxValidation(file, xsdIca);
            detail = "XML is valid against the XSD schema.";
            checkItemList.add(CheckItem.builder()
                    .title("XSD Schema")
                    .value(xsdIca)
                    .valid(CheckItem.Validity.VALID)
                    .note(detail)
                    .build()
            );

        } catch (SAXException | IOException | XMLStreamException e) {
            detail = getExceptionErrors(e.getMessage());
            checkItemList.add(CheckItem.builder()
                    .title("XSD Schema")
                    .value(xsdIca)
                    .valid(CheckItem.Validity.NOT_VALID)
                    .note(detail)
                    .build()
            );
            return checkItemList;
        }

        // map file into model class
        IcaXml xml = mapXml(file, IcaXml.class);

        // check PA
        String paFiscalCode = xml.getIdentificativoDominio();
        Pa pa = null;
        List<CodifichePa> encodings = null;
        try {
            pa = getPaIfExists(paFiscalCode);
            checkItemList.addAll(checkCi(pa, xml));

            // check qr-code
            encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
            checkItemList.add(checkQrCode(pa, encodings));

        } catch (AppException e) {
            checkItemList.add(CheckItem.builder()
                    .title("CI fiscal Code")
                    .value(paFiscalCode)
                    .valid(CheckItem.Validity.NOT_VALID)
                    .note("CI fiscal code not consistent")
                    .build());
        }

        // check flow
        if (pa != null) {
            checkItemList.add(checkFlow(xml, pa));
        }

        // check date
        checkItemList.add(checkValidityDate(xml.getDataInizioValidita()));

        // retrieve CI ibans
        List<IbanValidiPerPa> ibans = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
        checkItemList.addAll(checkIbans(xml.getContiDiAccredito(), ibans, encodings));

        return checkItemList;
    }

    private List<CheckItem> checkCi(Pa pa, IcaXml xml) {
        List<CheckItem> checkItemList = new ArrayList<>();
        checkItemList.add(CommonUtil.checkData("Creditor Institution", pa.getIdDominio(), xml.getIdentificativoDominio(), "PA fiscal code not consistent"));
        checkItemList.add(CommonUtil.checkData("Business Name", pa.getRagioneSociale(), xml.getRagioneSociale(), "Business name not consistent"));
        return checkItemList;
    }

    private List<CheckItem> checkIbans(List<Object> contiDiAccredito, List<IbanValidiPerPa> ibans, List<CodifichePa> encodings) {
        List<CheckItem> checkItemList = new ArrayList<>();
        contiDiAccredito.forEach(item -> {
            if (item.getClass().equals(String.class)) {
                String iban = (String) item;
                checkItemList.add(getIbanCheckItem(iban, ibans, encodings));
            }
            else if (item.getClass().equals(IcaXml.InfoContoDiAccreditoPair.class)) {
                String iban = ((IcaXml.InfoContoDiAccreditoPair) item).getIbanAccredito();
                checkItemList.add(getIbanCheckItem(iban, ibans, encodings));
            }
            else {
                throw new AppException(AppError.ICA_BAD_REQUEST, "Iban field not mapped");
            }
        });
        return checkItemList;
    }

    private CheckItem getIbanCheckItem(String iban, List<IbanValidiPerPa> ibans, List<CodifichePa> encodings) {
        boolean valid = IBANValidator.getInstance().isValid(iban);
        String note = null;
        String action = null;
        if (valid) {
            // check if iban is already been added
            boolean found = ibans.stream().anyMatch(i -> i.getIbanAccredito().equals(iban));
            if (found) {
                note = "Iban already added. ";

                String abiCode = iban.substring(5, 10);
                if (abiCode.equals("07601")) {
                    Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
                    note += result.get("note");
                    action = result.get("action");
                }
            }
            else {
                String abiCode = iban.substring(5, 10);
                // check if postal iban and then check that the related barcode-128-aim encoding exists
                note = "New Iban. ";
                if (abiCode.equals("07601")) {
                    Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
                    note += result.get("note");
                    action = result.get("action");
                }
            }
        } else {
            note = "Iban not valid";
        }
        return CheckItem.builder()
                .title("Iban")
                .value(iban)
                .valid(valid ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
                .note(note)
                .action(action)
                .build();
    }

    private Map<String, String> checkPostalCode(String ibanEncoding, List<CodifichePa> encodings) {
        boolean encodingFound = encodings.stream()
                .filter(encoding -> encoding.getFkCodifica().getIdCodifica().equals(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue()))
                .anyMatch(encoding -> encoding.getCodicePa().equals(ibanEncoding));
        Map<String, String> result = new HashMap<>();
        result.put("action", encodingFound ? "" : "ADD_ENCODING");
        result.put("note", encodingFound ? "Encoding already present." : "Encoding not found.");
        return result;
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
     * @param xml XML file
     * @param pa     the PA from DB
     */
    private CheckItem checkFlow(IcaXml xml, Pa pa) {
        Optional<InformativeContoAccreditoMaster> optFlow = informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(xml.getIdentificativoFlusso(), pa.getIdDominio());
        return CheckItem.builder()
                .title("Flow identifier")
                .value(xml.getIdentificativoFlusso())
                .valid(optFlow.isPresent() ? CheckItem.Validity.NOT_VALID : CheckItem.Validity.VALID)
                .note(optFlow.isPresent() ? "Flow identifier already exists" : "")
                .build();
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
     * @param validityDate check if the validity is after today
     * @return item with validity info
     */
    private CheckItem checkValidityDate(XMLGregorianCalendar validityDate) {
        var now = LocalDate.now();
        Timestamp tomorrow = Timestamp.valueOf(LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23, 59, 59));
        boolean valid = true;
        String details = "";
        if (!validityDate.toString().matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}")) {
            details += "Validity start date must be formatted as yyyy-MM-ddTHH:mm:ss. ";
            valid = false;
        }

        if (toTimestamp(validityDate).before(tomorrow)) {
            details += "Validity start date must be greater than the today's date";
            valid = false;
        }

        return CheckItem.builder()
                .title("Validity date")
                .value(validityDate.toString())
                .valid(valid ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
                .note(valid ? "" : details)
                .build();
    }

    /**
     * @param pa check if PA has QR-CODE encodings
     * @param codifichePaList
     */
    private CheckItem checkQrCode(Pa pa, List<CodifichePa> codifichePaList) {
        boolean hasQrcodeEncoding = codifichePaList.stream()
                .anyMatch(elem -> elem.getFkCodifica().getIdCodifica().equals("QR-CODE"));

        return CheckItem.builder()
                .title("QR Code")
                .value(pa.getIdDominio())
                .valid(hasQrcodeEncoding ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
                .note(hasQrcodeEncoding ? "Flow identifier already exists" : "")
                .action(hasQrcodeEncoding ? "" : "ADD_QRCODE")
                .build();
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
            syntaxValidation(file, xsdIca);
        } catch (SAXException | IOException | XMLStreamException e) {
            throw new AppException(AppError.ICA_BAD_REQUEST, e, e.getMessage());
        }
    }
}
