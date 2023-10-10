package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.MassiveCheck;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.*;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.*;
import it.gov.pagopa.apiconfig.starter.repository.*;
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
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.*;

@Service
@Validated
@Transactional
public class IcaService {

  public static final String ABI_IBAN_POSTALI = "07601";
  public static final String ACTION_KEY = "action";
  public static final String NOTE_KEY = "note";
  public static final String XSD_SCHEMA_TITLE = "XSD Schema";
  public static final String ICA_BAD_REQUEST = "ICA bad request";

  @Autowired private PaRepository paRepository;

  @Autowired
  private InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

  @Autowired private BinaryFileRepository binaryFileRepository;

  @Autowired
  private InformativeContoAccreditoDetailRepository informativeContoAccreditoDetailRepository;

  @Autowired private CodifichePaRepository codifichePaRepository;

  @Autowired private IbanValidiPerPaRepository ibanValidiPerPaRepository;

  @Autowired private ModelMapper modelMapper;

  @Value("${xsd.ica}")
  private String xsdIca;

  @Value("${zip.entries}")
  private int thresholdEntries; // Maximum number of entries allowed in the zip file

  @Value("${zip.size}")
  private int thresholdSize; // Max size of zip file content

  public Icas getIcas(
      @NotNull Integer limit,
      @NotNull Integer pageNumber,
      String idIca,
      String creditorInstitutionCode) {
    Pageable pageable =
        PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "dataInizioValidita"));
    var filters =
        CommonUtil.getFilters(
            InformativeContoAccreditoMaster.builder()
                .idInformativaContoAccreditoPa(idIca)
                .fkPa(Pa.builder().idDominio(creditorInstitutionCode).build())
                .build());
    Page<InformativeContoAccreditoMaster> page =
        informativeContoAccreditoMasterRepository.findAll(filters, pageable);
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

  public void createIca(@NotNull MultipartFile file, Boolean force) {
    try {
      createIca(new ByteArrayInputStream(file.getInputStream().readAllBytes()), force);
    } catch (SAXException | IOException e) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, ICA_BAD_REQUEST, "Problem when creating new ICA");
    }
  }

  public void createMassiveIcas(@NotNull MultipartFile file) {
    BiFunction<InputStream, Boolean, List<CheckItem>> func =
        (inputStream, k) -> {
          try {
            return createIca(inputStream, false);
          } catch (IOException | SAXException e) {
            throw new AppException(
                HttpStatus.BAD_REQUEST, ICA_BAD_REQUEST, "Problem in the file examination");
          }
        };
    massiveRead(file, func, false);
  }

  private List<CheckItem> createIca(@NotNull InputStream inputStream, Boolean force)
      throws SAXException, IOException {
    List<CheckItem> checks = verifyIca(inputStream, force);

    Optional<CheckItem> check =
        checks.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .findFirst();
    if (check.isPresent()) {
      throw new AppException(
          AppError.ICA_BAD_REQUEST,
          String.format("[%s] %s", check.get().getValue(), check.get().getNote()));
    }
    // map file into model class
    inputStream.reset();
    IcaXml icaXml = mapXml(inputStream, IcaXml.class);

    var pa = getPaIfExists(icaXml.getIdentificativoDominio());

    // save
    inputStream.reset();
    var binaryFile = saveBinaryFile(inputStream.readAllBytes(), inputStream.readAllBytes().length);
    var icaMaster = saveIcaMaster(icaXml, pa, binaryFile);
    for (Object elem : icaXml.getContiDiAccredito()) {
      saveIcaDetail(elem, icaMaster);
    }
    return new ArrayList<>();
  }

  public void deleteIca(String idIca, String creditorInstitutionCode) {
    var icaMaster = getIcaMasterIfExists(idIca, creditorInstitutionCode);
    var valid =
        informativeContoAccreditoMasterRepository
            .findByFkPa_IdDominioAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(
                creditorInstitutionCode, Timestamp.valueOf(LocalDateTime.now()));
    if (!valid.isEmpty() && valid.get(0).getId().equals(icaMaster.getId())) {
      throw new AppException(HttpStatus.CONFLICT, "ICA conflict", "This ICA is used.");
    }
    informativeContoAccreditoMasterRepository.delete(icaMaster);
  }

  public List<CheckItem> verifyIca(MultipartFile file, Boolean force) {
    try {
      return verifyIca(new ByteArrayInputStream(file.getInputStream().readAllBytes()), force);
    } catch (IOException | SAXException e) {
      List<CheckItem> checkItemList = new ArrayList<>();
      String detail = getExceptionErrors(e.getMessage());
      checkItemList.add(
          CheckItem.builder()
              .title(XSD_SCHEMA_TITLE)
              .value(xsdIca)
              .valid(CheckItem.Validity.NOT_VALID)
              .note(detail)
              .build());
      return checkItemList;
    }
  }

  public List<MassiveCheck> massiveVerifyIcas(MultipartFile file, boolean force) {
    BiFunction<InputStream, Boolean, List<CheckItem>> func =
        (inputStream, k) -> {
          try {
            return verifyIca(inputStream, force);
          } catch (SAXException e) {
            throw new AppException(
                HttpStatus.BAD_REQUEST, ICA_BAD_REQUEST, "Problem in the file examination");
          }
        };
    return massiveRead(file, func, force);
  }

  private List<CheckItem> verifyIca(InputStream inputStream, Boolean force) throws SAXException {
    // checks are described here
    // https://pagopa.atlassian.net/wiki/spaces/ACN/pages/441517396/Verifica+ICA
    List<CheckItem> checkItemList = new ArrayList<>();
    // syntax checks
    String detail;
    try {
      syntaxValidation(inputStream, xsdIca);
      inputStream.reset();
      detail = "XML is valid against the XSD schema.";
      checkItemList.add(
          CheckItem.builder()
              .title(XSD_SCHEMA_TITLE)
              .value(xsdIca)
              .valid(CheckItem.Validity.VALID)
              .note(detail)
              .build());
    } catch (XMLStreamException | IOException e) {
      detail = getExceptionErrors(e.getMessage());
      checkItemList.add(
          CheckItem.builder()
              .title(XSD_SCHEMA_TITLE)
              .value(xsdIca)
              .valid(CheckItem.Validity.NOT_VALID)
              .note(detail)
              .build());
      return checkItemList;
    }

    // map file into model class
    IcaXml xml = mapXml(inputStream, IcaXml.class);

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
      checkItemList.add(
          CheckItem.builder()
              .title("CI fiscal Code")
              .value(paFiscalCode)
              .valid(CheckItem.Validity.NOT_VALID)
              .note("CI fiscal code not consistent")
              .build());
    }

    if (pa != null) {
      // check flow
      checkItemList.add(checkFlow(xml, pa));

      // retrieve CI ibans
      List<IbanValidiPerPa> ibans = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
      checkItemList.addAll(checkIbans(xml.getContiDiAccredito(), ibans, encodings));
    }

    // check date
    if (Boolean.FALSE.equals(force)) {
      checkItemList.add(CommonUtil.checkValidityDate(xml.getDataInizioValidita()));
    }

    return checkItemList;
  }

  private List<CheckItem> checkCi(Pa pa, IcaXml xml) {
    List<CheckItem> checkItemList = new ArrayList<>();
    checkItemList.add(
        CommonUtil.checkData(
            "Creditor Institution",
            pa.getIdDominio(),
            xml.getIdentificativoDominio(),
            "PA fiscal code not consistent"));
    checkItemList.add(
        CommonUtil.checkData(
            "Business Name",
            pa.getRagioneSociale(),
            xml.getRagioneSociale(),
            "Business name not consistent"));
    return checkItemList;
  }

  private List<CheckItem> checkIbans(
      List<Object> contiDiAccredito, List<IbanValidiPerPa> ibans, List<CodifichePa> encodings) {
    List<CheckItem> checkItemList = new ArrayList<>();
    contiDiAccredito.forEach(
        item -> {
          if (item instanceof String) {
            String iban = (String) item;
            checkItemList.add(getIbanCheckItem(iban, ibans, encodings));
          } else if (item instanceof IcaXml.InfoContoDiAccreditoPair) {
            String iban = ((IcaXml.InfoContoDiAccreditoPair) item).getIbanAccredito();
            checkItemList.add(getIbanCheckItem(iban, ibans, encodings));
          } else {
            throw new AppException(AppError.ICA_BAD_REQUEST, "Iban field not mapped");
          }
        });
    return checkItemList;
  }

  private CheckItem getIbanCheckItem(
      String iban, List<IbanValidiPerPa> ibans, List<CodifichePa> encodings) {
    boolean valid = IBANValidator.getInstance().isValid(iban);
    String note;
    String action = null;

    if (valid) {
      // check if iban is already been added
      boolean found = ibans.stream().anyMatch(i -> i.getIbanAccredito().equals(iban));
      if (found) {
        note = "Iban already added. ";

        String abiCode = iban.substring(5, 10);
        if (abiCode.equals(ABI_IBAN_POSTALI)) {
          Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
          note += result.get(NOTE_KEY);
          action = result.get(ACTION_KEY);
        }
      } else {
        String abiCode = iban.substring(5, 10);
        // check if postal iban and then check that the related barcode-128-aim encoding exists
        note = "New Iban. ";
        if (abiCode.equals(ABI_IBAN_POSTALI)) {
          Map<String, String> result = checkPostalCode(iban.substring(15), encodings);
          note += result.get(NOTE_KEY);
          action = result.get(ACTION_KEY);
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
    boolean encodingFound =
        encodings.stream()
            .filter(
                encoding ->
                    encoding
                        .getFkCodifica()
                        .getIdCodifica()
                        .equals(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue()))
            .anyMatch(encoding -> encoding.getCodicePa().equals(ibanEncoding));
    Map<String, String> result = new HashMap<>();
    result.put(ACTION_KEY, encodingFound ? "" : "ADD_ENCODING");
    result.put(NOTE_KEY, encodingFound ? "Encoding already present." : "Encoding not found.");
    return result;
  }

  /**
   * @param idIca id ICA
   * @param creditorInstitutionCode id dominio
   * @return return the entity from the DB if exists
   */
  private InformativeContoAccreditoMaster getIcaMasterIfExists(
      String idIca, String creditorInstitutionCode) {
    return informativeContoAccreditoMasterRepository
        .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(idIca, creditorInstitutionCode)
        .orElseThrow(() -> new AppException(AppError.ICA_NOT_FOUND, idIca));
  }

  /**
   * check if flusso in the xml already exists
   *
   * @param xml XML file
   * @param pa the PA from DB
   */
  private CheckItem checkFlow(IcaXml xml, Pa pa) {
    Optional<InformativeContoAccreditoMaster> optFlow =
        informativeContoAccreditoMasterRepository
            .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(
                xml.getIdentificativoFlusso(), pa.getIdDominio());
    return CheckItem.builder()
        .title("Flow identifier")
        .value(xml.getIdentificativoFlusso())
        .valid(optFlow.isPresent() ? CheckItem.Validity.NOT_VALID : CheckItem.Validity.VALID)
        .note(optFlow.isPresent() ? "Flow identifier already exists" : "")
        .build();
  }

  /**
   * @param pa check if PA has QR-CODE encodings
   * @param codifichePaList
   */
  private CheckItem checkQrCode(Pa pa, List<CodifichePa> codifichePaList) {
    boolean hasQrcodeEncoding =
        codifichePaList.stream()
            .anyMatch(elem -> elem.getFkCodifica().getIdCodifica().equals("QR-CODE"));

    return CheckItem.builder()
        .title("QR Code")
        .value(pa.getIdDominio())
        .valid(hasQrcodeEncoding ? CheckItem.Validity.VALID : CheckItem.Validity.NOT_VALID)
        .note(hasQrcodeEncoding ? "QR-Code already exists" : "QR-Code not present")
        .action(hasQrcodeEncoding ? "" : "ADD_QRCODE")
        .build();
  }

  /**
   * @param creditorInstitutionCode identificativo Dominio
   * @return get the PA from DB using identificativoDominio
   */
  private Pa getPaIfExists(String creditorInstitutionCode) {
    return paRepository
        .findByIdDominio(creditorInstitutionCode)
        .orElseThrow(
            () ->
                new AppException(AppError.ICA_BAD_REQUEST, creditorInstitutionCode + " not found"));
  }

  /**
   * @param contoAccredito info icaDetail
   * @param icaMaster used for FK
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

    var icaDetail =
        InformativeContoAccreditoDetail.builder()
            .ibanAccredito(iban)
            .idBancaSeller(bank)
            .fkInformativaContoAccreditoMaster(icaMaster)
            .build();
    informativeContoAccreditoDetailRepository.save(icaDetail);
  }

  /**
   * @param icaXml info icaMaster
   * @param pa used for FK
   * @param binaryFile used for FK
   * @return the entity saved in the database
   */
  private InformativeContoAccreditoMaster saveIcaMaster(
      IcaXml icaXml, Pa pa, BinaryFile binaryFile) {
    var icaMaster =
        InformativeContoAccreditoMaster.builder()
            .dataInizioValidita(Timestamp.valueOf(icaXml.getDataInizioValidita()))
            .dataPubblicazione(Timestamp.valueOf(icaXml.getDataPubblicazione()))
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
  private BinaryFile saveBinaryFile(byte[] file, long size) {
    BinaryFile binaryFile;
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(file);
      binaryFile =
          BinaryFile.builder().fileContent(file).fileSize(size).fileHash(md.digest()).build();
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
    return page.stream().map(elem -> modelMapper.map(elem, Ica.class)).collect(Collectors.toList());
  }

  /**
   * Helper for the massive read
   *
   * @param file MultiPartFile to analyze
   * @param callback function to apply to file
   * @param force boolean to bypass date verification
   * @return a List of massiveChecks corresponding to the zip entry.
   */
  @java.lang.SuppressWarnings("java:S5042")
  private List<MassiveCheck> massiveRead(
      MultipartFile file,
      BiFunction<InputStream, Boolean, List<CheckItem>> callback,
      boolean force) {
    List<MassiveCheck> massiveChecks = new ArrayList<>();
    try {
      // bytes and number of files counter
      var bytesCounter =
          new Object() {
            int totalBytes = 0;
          };
      int nOfZipFiles = 0;

      // function to execute input callback during the analysis of zip files
      BiFunction<InputStream, Integer, List<CheckItem>> callbackCaller =
          (inputStream, nBytesOfEntries) -> {
            bytesCounter.totalBytes += nBytesOfEntries;

            if (bytesCounter.totalBytes > thresholdSize) {
              throw new AppException(
                  HttpStatus.BAD_REQUEST, ICA_BAD_REQUEST, "Zip content too large");
            }

            return callback.apply(inputStream, force);
          };

      // unzip the input stream
      ZipInputStream zis = new ZipInputStream(file.getInputStream());
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        ++nOfZipFiles;
        if (nOfZipFiles > thresholdEntries) {
          throw new AppException(
              HttpStatus.BAD_REQUEST,
              ICA_BAD_REQUEST,
              "Zip content has too many entries (check for hidden files)");
        }

        List<CheckItem> listToAdd = zipReading(zipEntry, zis, callbackCaller);

        // empty if file is a hidden one or a folder
        if (!listToAdd.isEmpty()) {
          massiveChecks.add(
              MassiveCheck.builder().fileName(zipEntry.getName()).checkItems(listToAdd).build());
        }
        // go to next file inside zip
        zipEntry = zis.getNextEntry();
      }
    } catch (IOException e) {
      throw new AppException(
          HttpStatus.BAD_REQUEST, ICA_BAD_REQUEST, "Problem when unzipping file", e);
    }
    return massiveChecks;
  }

  /**
   * Open zipEntry content and put it on an outputstream
   *
   * @param zipEntry entry of the zipFile
   * @param zis zip file content
   * @return a List of checkItems corresponding to the zip entry.
   */
  // added to avoid sonar warning, we need to use tempFile to avoid to analyze hidden files and
  // directories
  @java.lang.SuppressWarnings({"javasecurity:S6096", "java:S5443"})
  private List<CheckItem> zipReading(
      ZipEntry zipEntry,
      ZipInputStream zis,
      BiFunction<InputStream, Integer, List<CheckItem>> callback)
      throws IOException {
    File tempFile = File.createTempFile(zipEntry.getName(), "xml");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int nBytes = 0;
    if (!tempFile.isHidden() && !zipEntry.isDirectory()) {
      for (int c = zis.read(); c != -1; c = zis.read()) {
        baos.write(c);
        ++nBytes;
      }
      Files.delete(tempFile.toPath());
      return callback.apply(new ByteArrayInputStream(baos.toByteArray()), nBytes);
    }
    // remove temp file
    Files.delete(tempFile.toPath());
    return new ArrayList<>();
  }
}
