package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.getExceptionErrors;
import static it.gov.pagopa.apiconfig.core.util.CommonUtil.mapXml;
import static it.gov.pagopa.apiconfig.core.util.CommonUtil.syntaxValidation;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.psp.Cdi;
import it.gov.pagopa.apiconfig.core.model.psp.CdiXml;
import it.gov.pagopa.apiconfig.core.model.psp.Cdis;
import it.gov.pagopa.apiconfig.core.util.AFMUtilsAsyncTask;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.BinaryFile;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiFasciaCostoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import it.gov.pagopa.apiconfig.starter.entity.CdiPreference;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.repository.BinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.CanaliRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiDetailRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiFasciaCostoServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiInformazioniServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiPreferenceRepository;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPspRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspCanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspRepository;
import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.stream.XMLStreamException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Service
@Validated
@Slf4j
public class CdiService {

  @Autowired private CdiMasterRepository cdiMasterRepository;
  @Autowired PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

  @Autowired PspRepository pspRepository;

  @Autowired CanaliRepository canaliRepository;

  @Autowired IntermediariPspRepository intermediariPspRepository;

  @Autowired BinaryFileRepository binaryFileRepository;

  @Autowired private CdiDetailRepository cdiDetailRepository;

  @Autowired private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;

  @Autowired private CdiFasciaCostoServizioRepository cdiFasciaCostoServizioRepository;

  @Autowired CdiPreferenceRepository cdiPreferenceRepository;

  @Autowired private ModelMapper modelMapper;
  @Autowired private AFMUtilsAsyncTask afmUtilsAsyncTask;

  @Value("${xsd.cdi}")
  private String xsdCdi;

  @Transactional(readOnly = true)
  public Cdis getCdis(
      @NotNull Integer limit, @NotNull Integer pageNumber, String idCdi, String pspCode) {
    Pageable pageable =
        PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "dataInizioValidita"));
    var filters =
        CommonUtil.getFilters(
            CdiMaster.builder()
                .idInformativaPsp(idCdi)
                .fkPsp(Psp.builder().idPsp(pspCode).build())
                .build());
    Page<CdiMaster> page = cdiMasterRepository.findAll(filters, pageable);
    return Cdis.builder()
        .cdiList(getCdiList(page))
        .pageInfo(CommonUtil.buildPageInfo(page))
        .build();
  }

  @Transactional(readOnly = true)
  public byte[] getCdi(@NotBlank String idCdi, @NotBlank String pspCode) {
    CdiMaster cdiMaster =
        cdiMasterRepository
            .findByIdInformativaPspAndFkPsp_IdPsp(idCdi, pspCode)
            .orElseThrow(() -> new AppException(AppError.CDI_NOT_FOUND, idCdi));

    return cdiMaster.getFkBinaryFile().getFileContent();
  }

  @Transactional
  public void createCdi(MultipartFile file) {
    List<CheckItem> checks = verifyCdi(file);

    Optional<CheckItem> check =
        checks.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .findFirst();
    if (check.isPresent()) {
      throw new AppException(
          AppError.CDI_BAD_REQUEST,
          String.format("[%s] %s", check.get().getValue(), check.get().getNote()));
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

    // send CDI to AFM Utils
    afmUtilsAsyncTask.executeSync(master);
  }

  @Transactional
  public void deleteCdi(String idCdi, String pspCode) {
    CdiMaster cdiMaster =
        cdiMasterRepository
            .findByIdInformativaPspAndFkPsp_IdPsp(idCdi, pspCode)
            .orElseThrow(() -> new AppException(AppError.CDI_NOT_FOUND, idCdi));

    var valid =
        cdiMasterRepository
            .findByFkPsp_IdPspAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(
                pspCode, Timestamp.valueOf(LocalDateTime.now()));
    if (!valid.isEmpty() && valid.get(0).getId().equals(cdiMaster.getId())) {
      throw new AppException(HttpStatus.CONFLICT, "CDI conflict", "This CDI is used.");
    }

    try {
      cdiMasterRepository.delete(cdiMaster);
      // deletion of the bundles associated with the CDI by calling AFM Utils
      afmUtilsAsyncTask.afmUtilsDeleteBundlesByIdCDI(idCdi, pspCode);
    } catch (Exception e) {
      log.error(
          "Error while deleting the CDI or the associated bundles [idCdi="
              + idCdi
              + ", pspCode="
              + pspCode
              + "]",
          e);
      throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }
  }

  @Transactional(readOnly = true, propagation = Propagation.NESTED)
  public void uploadHistory() {
    CompletableFuture.runAsync(() -> afmUtilsAsyncTask.executeSync());
  }

  @Transactional(readOnly = true)
  public List<CheckItem> verifyCdi(MultipartFile file) {
    // checks are described here
    // https://pagopa.atlassian.net/wiki/spaces/ACN/pages/467435579/Verifica+CDI

    List<CheckItem> checkItemList = new ArrayList<>();
    // syntax checks
    String detail;
    try {
      syntaxValidation(file, xsdCdi);
      detail = "XML is valid against the XSD schema.";
      checkItemList.add(
          CheckItem.builder()
              .title("XSD Schema")
              .value(xsdCdi)
              .valid(CheckItem.Validity.VALID)
              .note(detail)
              .build());
    } catch (SAXException | IOException | XMLStreamException e) {
      detail = getExceptionErrors(e.getMessage());
      checkItemList.add(
          CheckItem.builder()
              .title("XSD Schema")
              .value(xsdCdi)
              .valid(CheckItem.Validity.NOT_VALID)
              .note(detail)
              .build());
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
      CheckItem checkItem =
          CommonUtil.checkData(
              "Stamp",
              xml.getInformativaMaster().getMarcaBolloDigitale(),
              psp.getMarcaBolloDigitale(),
              "Stamp not consistent");
      checkItemList.add(checkItem);
    } catch (AppException e) {
      checkItemList.add(
          CheckItem.builder()
              .title("PSP Identifier")
              .value(pspId)
              .valid(CheckItem.Validity.NOT_VALID)
              .note("PSP identifier not consistent")
              .build());
    }

    // check date
    checkItemList.add(
        CommonUtil.checkValidityDate(xml.getInformativaMaster().getDataInizioValidita()));

    // check flow id
    if (psp != null) {
      checkItemList.add(checkFlow(xml, psp));
    }

    for (CdiXml.InformativaDetail informativaDetail :
        xml.getListaInformativaDetail().getInformativaDetail()) {
      // check channel and paymentMethod
      String channelId = informativaDetail.getIdentificativoCanale();
      Optional<Canali> channel = canaliRepository.findByIdCanale(channelId);
      if (channel.isEmpty()) {
        checkItemList.add(
            CheckItem.builder()
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
    checkItemList.add(
        CommonUtil.checkData("PSP", psp.getIdPsp(), pspId, "PSP identifier not consistent"));
    checkItemList.add(
        CommonUtil.checkData(
            "Business Name",
            psp.getRagioneSociale(),
            xml.getRagioneSociale(),
            "Business name not consistent"));
    checkItemList.add(
        CommonUtil.checkData(
            "ABI Code", psp.getAbi(), xml.getCodiceABI(), "ABI code not consistent"));
    checkItemList.add(
        CommonUtil.checkData(
            "BIC Code", psp.getBic(), xml.getCodiceBIC(), "BIC code not consistent"));
    return checkItemList;
  }

  private CheckItem checkPaymentMethods(
      Canali channel, Psp psp, CdiXml.InformativaDetail informativaDetail) {
    List<PspCanaleTipoVersamento> paymentMethods =
        pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            psp.getObjId(), channel.getId());
    String paymentType =
        informativaDetail.getTipoVersamento() != null
            ? informativaDetail.getTipoVersamento()
            : "PO";

    CheckItem.Validity validity =
        paymentMethods.stream()
                .anyMatch(
                    pm ->
                        pm.getCanaleTipoVersamento()
                            .getTipoVersamento()
                            .getTipoVersamento()
                            .equals(paymentType))
            ? CheckItem.Validity.VALID
            : CheckItem.Validity.NOT_VALID;
    return CheckItem.builder()
        .title("Channel - Payment Method")
        .value(channel.getIdCanale() + " - " + informativaDetail.getTipoVersamento())
        .valid(validity)
        .note(
            validity.equals(CheckItem.Validity.VALID)
                ? ""
                : "Channel and/or payment method not consistent")
        .build();
  }

  private List<CheckItem> checkLanguages(CdiXml.InformativaDetail informativaDetail) {
    List<String> languages = new ArrayList<>();
    List<CheckItem> checkItemList = new ArrayList<>();
    String title = "Language code";

    for (CdiXml.InformazioniServizio informazioniServizio :
        informativaDetail.getListaInformazioniServizio().getInformazioniServizio()) {
      languages.add(informazioniServizio.getCodiceLingua());
    }

    List<String> languagesTarget =
        Stream.of("IT", "EN", "DE", "FR", "SL").collect(Collectors.toList());
    languagesTarget.removeAll(languages);

    final boolean[] duplicate = {false};

    Map<String, Long> frequencyMap =
        languages.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    frequencyMap.entrySet().stream()
        .filter(item -> item.getValue() > 1)
        .forEach(
            item -> {
              checkItemList.add(
                  CheckItem.builder()
                      .title(title)
                      .value(item.getKey())
                      .valid(CheckItem.Validity.NOT_VALID)
                      .note(String.format("%s occurrences", item.getValue()))
                      .build());
              duplicate[0] = true;
            });

    if (languagesTarget.isEmpty() && !duplicate[0]) {
      checkItemList.add(
          CheckItem.builder().title(title).value("").valid(CheckItem.Validity.VALID).build());
    } else if (!languagesTarget.isEmpty()) {
      checkItemList.add(
          CheckItem.builder()
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
    for (CdiXml.FasciaCostoServizio maxServiceAmount :
        informativaDetail
            .getCostiServizio()
            .getListaFasceCostoServizio()
            .getFasciaCostoServizio()) {
      if (maxServiceAmountList.contains(maxServiceAmount.getImportoMassimoFascia())) {
        checkItemList.add(
            CheckItem.builder()
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
      checkItemList.add(
          CheckItem.builder()
              .title("Maximum amount ranges")
              .value("")
              .valid(CheckItem.Validity.VALID)
              .build());
    }
    return checkItemList;
  }

  private CheckItem checkBrokerPsp(Canali channel, CdiXml.InformativaDetail informativaDetail) {
    String brokerPsp = informativaDetail.getIdentificativoIntermediario();
    CheckItem.Validity validity =
        channel.getFkIntermediarioPsp().getIdIntermediarioPsp().equals(brokerPsp)
            ? CheckItem.Validity.VALID
            : CheckItem.Validity.NOT_VALID;
    return CheckItem.builder()
        .title("Broker PSP")
        .value(brokerPsp)
        .valid(validity)
        .note(
            validity.equals(CheckItem.Validity.VALID)
                ? ""
                : "Broker Psp not related to the channel")
        .build();
  }

  private CheckItem checkPaymentModel(CdiXml.InformativaDetail informativaDetail) {
    CheckItem.Validity validity =
        informativaDetail.getTipoVersamento().equals("PO")
            ? CheckItem.Validity.VALID
            : CheckItem.Validity.NOT_VALID;
    return CheckItem.builder()
        .title("Payment model")
        .value(informativaDetail.getModelloPagamento().toString())
        .valid(validity)
        .note(
            validity.equals(CheckItem.Validity.VALID)
                ? ""
                : "Payment model not related to payment method")
        .build();
  }

  private CheckItem checkFlow(CdiXml xml, Psp psp) {
    Optional<CdiMaster> optFlow =
        cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(
            xml.getIdentificativoFlusso(), psp.getIdPsp());
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
   * @param xml CDI file
   * @param xmlDetail informativaDetail: detail element to save
   * @param detail FK CdiDetail
   */
  private void saveCdiPreferences(
      @NotNull CdiXml xml, @NotNull CdiXml.InformativaDetail xmlDetail, @NotNull CdiDetail detail) {
    if (xmlDetail.getListaConvenzioni() != null) {
      xmlDetail
          .getListaConvenzioni()
          .forEach(
              elem -> {
                double costoConvenzione =
                    xmlDetail.getCostiServizio() != null
                            && xmlDetail.getCostiServizio().getCostoConvenzione() != null
                        ? xmlDetail.getCostiServizio().getCostoConvenzione()
                        : 0;
                cdiPreferenceRepository.save(
                    CdiPreference.builder()
                        .cdiDetail(detail)
                        .seller(xml.getMybankIDVS())
                        .buyer(elem.getCodiceConvenzione())
                        .costoConvenzione(costoConvenzione)
                        .build());
              });
    }
  }

  /**
   * @param detail info detail in the XML
   * @param detailEntity FK CdiDetail
   */
  private void saveCdiFasciaCostiServizio(
      @NotNull CdiXml.InformativaDetail detail, @NotNull CdiDetail detailEntity) {
    CdiXml.CostiServizio costiServizio = detail.getCostiServizio();
    if (costiServizio != null
        && costiServizio.getListaFasceCostoServizio() != null
        && costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio() != null) {
      // sort by importoMassimo and create fascia costi servizio
      var importi =
          costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().stream()
              .map(CdiXml.FasciaCostoServizio::getImportoMassimoFascia)
              .sorted(Double::compareTo)
              .distinct()
              .collect(Collectors.toList());
      // for each fascia save an element in the database
      for (int i = 0;
          i < costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().size();
          i++) {
        var fascia = costiServizio.getListaFasceCostoServizio().getFasciaCostoServizio().get(i);
        // importoMinimo is equals to previous importoMassimo (equals to 0 for the first element)
        var prev =
            importi.stream()
                .filter(elem -> elem < fascia.getImportoMassimoFascia())
                .max(Double::compareTo)
                .orElse(0.0);
        CdiXml.ConvenzioniCosti convenzione = null;
        if (fascia.getListaConvenzioniCosti() != null) {
          convenzione = fascia.getListaConvenzioniCosti().stream().findFirst().orElse(null);
        }
        cdiFasciaCostoServizioRepository.save(
            CdiFasciaCostoServizio.builder()
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
   * @param detail info detail in the XML
   * @param detailEntity FK CdiDetail
   */
  private void saveCdiInformazioniServizio(
      @NotNull CdiXml.InformativaDetail detail, @NotNull CdiDetail detailEntity) {
    if (detail.getListaInformazioniServizio() != null
        && detail.getListaInformazioniServizio().getInformazioniServizio() != null) {
      for (var info : detail.getListaInformazioniServizio().getInformazioniServizio()) {
        cdiInformazioniServizioRepository.save(
            CdiInformazioniServizio.builder()
                .codiceLingua(info.getCodiceLingua() != null ? info.getCodiceLingua() : "IT")
                .descrizioneServizio(
                    info.getDescrizioneServizio() != null
                        ? info.getDescrizioneServizio()
                        : "Pagamento con CBILL")
                .disponibilitaServizio(
                    info.getDisponibilitaServizio() != null
                        ? info.getDisponibilitaServizio()
                        : "24/7/7")
                .urlInformazioniCanale(info.getUrlInformazioniCanale())
                .fkCdiDetail(detailEntity)
                .limitazioniServizio(info.getLimitazioniServizio())
                .build());
      }
    }
  }

  /**
   * @param master CdiMaster entity
   * @param detail Info Detail of XML
   * @param pspCanaleTipoVersamento FK PspCanaleTipoVersamento
   * @return save and return a CdiDetail entity
   */
  private CdiDetail saveCdiDetail(
      @NotNull CdiMaster master,
      @NotNull CdiXml.InformativaDetail detail,
      @NotNull PspCanaleTipoVersamento pspCanaleTipoVersamento) {
    CdiDetail.CdiDetailBuilder builder =
        CdiDetail.builder()
            .priorita(detail.getPriorita() != null ? detail.getPriorita() : 0L)
            .modelloPagamento(
                detail.getModelloPagamento() != null ? detail.getModelloPagamento() : 4L)
            .fkCdiMaster(master)
            .fkPspCanaleTipoVersamento(pspCanaleTipoVersamento)
            .canaleApp(detail.getCanaleApp() != null ? detail.getCanaleApp() : 0L);
    var identificazioneServizio = detail.getIdentificazioneServizio();
    if (identificazioneServizio != null) {
      builder
          .nomeServizio(identificazioneServizio.getNomeServizio())
          .logoServizio(
              identificazioneServizio.getLogoServizio() != null
                  ? identificazioneServizio.getLogoServizio().strip().getBytes()
                  : null);
    }
    if (detail.getListaParoleChiave() != null) {
      // join list of ParolaChiave in a string semicolon separated ([tag1, tag2,tag3] ->
      // "tag1;tag2;tag3")
      String tags =
          detail.getListaParoleChiave().stream()
              .filter(Objects::nonNull)
              .reduce((a, b) -> a + ";" + b)
              .orElse(null);
      builder.tags(tags);
    }
    return cdiDetailRepository.save(builder.build());
  }

  /**
   * @param psp entity PSP
   * @param detail InformativaDetail
   * @return save in DB and return a PspCanaleTipoVersamento
   */
  private PspCanaleTipoVersamento findPspCanaleTipoVersamentoIfExists(
      @NotNull Psp psp, @NotNull CdiXml.InformativaDetail detail) {
    String paymentType = detail.getTipoVersamento() != null ? detail.getTipoVersamento() : "PO";
    return pspCanaleTipoVersamentoRepository
        .findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(
            psp.getObjId(), detail.getIdentificativoCanale(), paymentType)
        .orElseThrow(
            () ->
                new AppException(
                    AppError.CDI_BAD_REQUEST, "tipoVersamento: " + paymentType + " not found"));
  }

  /**
   * save CdiMaster in DB and return the entity
   *
   * @param xml CDI file
   * @param psp FK PSP
   * @param binaryFile FK BinaryFile
   * @return entity saved in DB
   */
  private CdiMaster saveCdiMaster(CdiXml xml, Psp psp, BinaryFile binaryFile) {
    return cdiMasterRepository.save(
        CdiMaster.builder()
            .dataPubblicazione(Timestamp.valueOf(xml.getInformativaMaster().getDataPubblicazione()))
            .dataInizioValidita(
                Timestamp.valueOf(xml.getInformativaMaster().getDataInizioValidita()))
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
    return page.stream().map(elem -> modelMapper.map(elem, Cdi.class)).collect(Collectors.toList());
  }

  /**
   * @param pspCode Code of the payment service provider
   * @return the PSP record from DB if Exists
   */
  private Psp getPspIfExists(String pspCode) {
    return pspRepository
        .findByIdPsp(pspCode)
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
      binaryFile =
          BinaryFile.builder()
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
