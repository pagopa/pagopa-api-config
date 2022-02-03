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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getAbiFromIban;
import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getCcFromIban;
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

    public Icas getIcas(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<InformativeContoAccreditoMaster> page = informativeContoAccreditoMasterRepository.findAll(pageable);
        return Icas.builder()
                .icaList(getIcaList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public byte[] getIca(@NotNull String idIca, String creditorInstitutionCode) {
        Optional<InformativeContoAccreditoMaster> result = informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(idIca, creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(AppError.ICA_NOT_FOUND, idIca);
        }
        return result.get().getFkBinaryFile().getFileContent();
    }

    public XSDValidation verifyXSD(MultipartFile xml) {
        boolean xsdEvaluated = false;
        String lineNumber = "";
        String detail;

        try {
            syntacticValidationIcaXml(xml);
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
        try {
            syntacticValidationIcaXml(file);
        } catch (SAXException | IOException | XMLStreamException e) {
            throw new AppException(AppError.ICA_BAD_REQUEST, e, e.getMessage());
        }

        // map into model class
        IcaXml icaXml = mapIcaXml(file);

        // semantics checks
        var pa = getPaIfExists(icaXml);
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
     * @param icaXml XML file with identificativoDominio
     * @return get the PA from DB using identificativoDominio
     */
    private Pa getPaIfExists(IcaXml icaXml) {
        return paRepository.findByIdDominio(icaXml.getIdentificativoDominio())
                .orElseThrow(() -> new AppException(AppError.ICA_BAD_REQUEST, icaXml.getIdentificativoDominio() + " not found"));
    }

    /**
     * @param file ICA XML to map
     * @return XML mapped in the model
     */
    private IcaXml mapIcaXml(MultipartFile file) {
        IcaXml icaXml;
        try {
            JAXBContext context = JAXBContext.newInstance(IcaXml.class);
            icaXml = (IcaXml) context.createUnmarshaller()
                    .unmarshal(file.getInputStream());
        } catch (IOException | JAXBException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
        return icaXml;
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
     * @param xml file ICA to validate
     * @throws SAXException       if XML is not valid
     * @throws IOException        if XSD schema not found
     * @throws XMLStreamException error during read XML
     */
    private void syntacticValidationIcaXml(MultipartFile xml) throws SAXException, IOException, XMLStreamException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // to be compliant, prohibit the use of all protocols by external entities:
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        javax.xml.validation.Schema schema = factory.newSchema(new URL(xsdIca));
        Validator validator = schema.newValidator();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        InputStream inputStream = xml.getInputStream();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
        StAXSource source = new StAXSource(xmlStreamReader);
        validator.validate(source);
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
}
