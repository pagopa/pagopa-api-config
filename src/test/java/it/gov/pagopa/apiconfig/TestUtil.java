package it.gov.pagopa.apiconfig;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import it.gov.pagopa.apiconfig.core.model.configuration.AfmMarketplacePaymentType;
import it.gov.pagopa.apiconfig.core.model.configuration.CacheVersions;
import it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKeys;
import it.gov.pagopa.apiconfig.core.model.configuration.FtpServer;
import it.gov.pagopa.apiconfig.core.model.configuration.FtpServers;
import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.core.model.configuration.PaymentTypes;
import it.gov.pagopa.apiconfig.core.model.configuration.Pdd;
import it.gov.pagopa.apiconfig.core.model.configuration.Pdds;
import it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConfs;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.BrokerDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Brokers;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CounterpartTable;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CounterpartTables;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionAddress;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionEncodings;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStation;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationList;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionView;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutions;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionsView;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ibans;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ica;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Icas;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Protocol;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Stations;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.XSDValidation;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Filter;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.core.model.filterandorder.OrderType;
import it.gov.pagopa.apiconfig.core.model.psp.BrokerPspDetails;
import it.gov.pagopa.apiconfig.core.model.psp.BrokersPsp;
import it.gov.pagopa.apiconfig.core.model.psp.Cdi;
import it.gov.pagopa.apiconfig.core.model.psp.Cdis;
import it.gov.pagopa.apiconfig.core.model.psp.ChannelDetails;
import it.gov.pagopa.apiconfig.core.model.psp.ChannelPsp;
import it.gov.pagopa.apiconfig.core.model.psp.ChannelPspList;
import it.gov.pagopa.apiconfig.core.model.psp.Channels;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderView;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviders;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProvidersView;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannel;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelCode;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelList;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.core.model.psp.Service;
import it.gov.pagopa.apiconfig.core.model.psp.Services;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.BinaryFile;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CanaliNodo;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiFasciaCostoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.ElencoServizi;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.IcaBinaryFile;
import it.gov.pagopa.apiconfig.starter.entity.InformativeContoAccreditoMaster;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaDetail;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

@UtilityClass
public class TestUtil {

  /**
   * @param relativePath Path from source root of the json file
   * @return the Json string read from the file
   * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable
   *     byte sequence is read
   */
  public String readJsonFromFile(String relativePath) throws IOException {
    ClassLoader classLoader = TestUtil.class.getClassLoader();
    File file = new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getPath());
    return Files.readString(file.toPath());
  }

  /**
   * @param relativePath Path from source root of the file
   * @return the requested file
   */
  public File readFile(String relativePath) {
    ClassLoader classLoader = TestUtil.class.getClassLoader();
    return new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getFile());
  }

  /**
   * @param object to map into the Json string
   * @return object as Json string
   * @throws JsonProcessingException if there is an error during the parsing of the object
   */
  public String toJson(Object object) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(object);
  }

  /**
   * Prepare a Mock for the class {@link Page}
   *
   * @param content list of elements in the page to return by mock
   * @param limit number of elements per page to return by mock
   * @param pageNumber number of page to return by mock
   * @param <T> Class of the elements
   * @return a Mock of {@link Page}
   */
  public <T> Page<T> mockPage(List<T> content, int limit, int pageNumber) {
    @SuppressWarnings("unchecked")
    Page<T> page = Mockito.mock(Page.class);
    when(page.getTotalPages()).thenReturn((int) Math.ceil((double) content.size() / limit));
    when(page.getNumberOfElements()).thenReturn(content.size());
    when(page.getNumber()).thenReturn(pageNumber);
    when(page.getSize()).thenReturn(limit);
    when(page.getContent()).thenReturn(content);
    when(page.stream()).thenReturn(content.stream());
    return page;
  }

  public static Stazioni getMockStazioni() {
    return Stazioni.builder()
        .objId(2L)
        .idStazione("80007580279_01")
        .enabled(true)
        .versione(1L)
        .ip("NodoDeiPagamentiDellaPATest.sia.eu")
        .password("password")
        .porta(80L)
        .redirectIp("paygov.collaudo.regione.veneto.it")
        .redirectPath("nodo-regionale-fesp/paaInviaRispostaPagamento.html")
        .redirectPorta(443L)
        .servizio("openspcoop/PD/RT6TPDREGVENETO")
        .rtEnabled(true)
        .servizioPof("openspcoop/PD/CCP6TPDREGVENETO")
        .intermediarioPa(getMockIntermediariePa())
        .fkIntermediarioPa(1L)
        .redirectProtocollo("HTTPS")
        .proxyEnabled(true)
        .proxyHost("10.101.1.95")
        .proxyPort(8080L)
        .numThread(2L)
        .timeoutA(15L)
        .timeoutB(30L)
        .timeoutC(120L)
        .flagOnline(true)
        .protocollo("HTTPS")
        .protocollo4Mod("HTTPS")
        .invioRtIstantaneo(false)
        .targetHost("localhost")
        .targetPort(443L)
        .targetPath("/")
        .targetHostPof("localhost")
        .targetPortPof(443L)
        .targetPathPof("/")
        .versionePrimitive(1)
        .build();
  }

  public static Pa getMockPa() {
    return Pa.builder()
        .objId(1L)
        .idDominio("00168480242")
        .enabled(true)
        .ragioneSociale("Comune di Bassano del Grappa")
        .capDomicilioFiscale("00111")
        .pagamentoPressoPsp(true)
        .rendicontazioneFtp(false)
        .rendicontazioneZip(false)
        .build();
  }

  public static Pa getMockPa2() {
    return Pa.builder()
        .objId(2L)
        .idDominio("00168480243")
        .enabled(true)
        .ragioneSociale("Comune di Firenze")
        .capDomicilioFiscale("00112")
        .pagamentoPressoPsp(true)
        .rendicontazioneFtp(false)
        .rendicontazioneZip(false)
        .build();
  }

  public static PaStazionePa getMockPaStazionePa() {
    return PaStazionePa.builder()
        .pa(getMockPa())
        .fkPa(getMockPa().getObjId())
        .fkStazione(getMockStazioni())
        .broadcast(false)
        .auxDigit(1L)
        .progressivo(2L)
        .quartoModello(true)
        .segregazione(3L)
        .build();
  }

  public static CodifichePa getMockCodifichePa() {
    return CodifichePa.builder()
        .id(1L)
        .codicePa("1234")
        .fkCodifica(
            Codifiche.builder()
                .objId(2L)
                .idCodifica(Encoding.CodeTypeEnum.QR_CODE.getValue())
                .build())
        .build();
  }

  public static IbanValidiPerPa getMockIbanValidiPerPa() {
    return IbanValidiPerPa.builder()
        .fkPa(1L)
        .dataInizioValidita(Timestamp.valueOf("2017-03-09 00:00:00"))
        .dataPubblicazione(Timestamp.valueOf("2017-03-08 00:00:00"))
        .build();
  }

  public static IntermediariPa getMockIntermediariePa() {
    return IntermediariPa.builder()
        .objId(1L)
        .codiceIntermediario("Regione Lazio")
        .enabled(true)
        .faultBeanEsteso(true)
        .idIntermediarioPa("1234")
        .build();
  }

  public static InformativeContoAccreditoMaster getMockInformativeContoAccreditoMaster() {
    return InformativeContoAccreditoMaster.builder()
        .idInformativaContoAccreditoPa("111")
        .fkPa(getMockPa())
        .fkBinaryFile(getMockBinaryFile())
        .dataInizioValidita(Timestamp.valueOf("2017-03-09 00:00:00"))
        .dataPubblicazione(Timestamp.valueOf("2017-03-09 00:00:00"))
        .build();
  }

  public static BinaryFile getMockBinaryFile() {
    return BinaryFile.builder().id(1L).fileSize(2L).fileContent(new byte[] {1, 11}).build();
  }

  public static InformativePaMaster getMockInformativePaMaster() {
    return InformativePaMaster.builder()
        .idInformativaPa("111")
        .fkPa(getMockPa())
        .fkBinaryFile(getMockBinaryFile())
        .pagamentiPressoPsp(true)
        .dataInizioValidita(Timestamp.valueOf("2017-03-09 00:00:00"))
        .dataPubblicazione(Timestamp.valueOf("2017-03-09 00:00:00"))
        .build();
  }

  public static InformativePaDetail getMockInformativePaDetail() {
    return InformativePaDetail.builder()
        .id(1L)
        .giorno("lunedì")
        .tipo("settimanale")
        .flagDisponibilita(true)
        .fkInformativaPaMaster(getMockInformativePaMaster())
        .build();
  }

  public static CreditorInstitutionDetails getMockCreditorInstitutionDetails() {
    return CreditorInstitutionDetails.builder()
        .pspPayment(false)
        .creditorInstitutionCode("1234")
        .businessName("Città di Roma")
        .enabled(true)
        .address(CreditorInstitutionAddress.builder().city("Roma").zipCode("00111").build())
        .reportingFtp(true)
        .reportingZip(true)
        .build();
  }

  public static BrokerDetails getMockBrokerDetails() {
    return BrokerDetails.builder()
        .brokerCode("1234")
        .enabled(true)
        .description("Regione Lazio")
        .extendedFaultBean(false)
        .build();
  }

  public static Brokers getMockBrokers() {
    return Brokers.builder()
        .brokerList(List.of(getMockBrokerDetails()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static Stations getMockStations() {
    return Stations.builder()
        .stationsList(List.of(getMockStationDetails()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static StationDetails getMockStationDetails() {
    return StationDetails.builder()
        .stationCode("1234")
        .ip("1.1.1.1")
        .port(80L)
        .protocol(Protocol.HTTP)
        .password("pass")
        .timeoutA(1L)
        .timeoutB(1L)
        .timeoutC(1L)
        .version(1L)
        .enabled(true)
        .brokerCode("4321")
        .flagOnline(true)
        .ip4Mod("2.2.2.2")
        .service("/api")
        .threadNumber(2L)
        .rtInstantaneousDispatch(false)
        .primitiveVersion(1)
        .build();
  }

  public static StationCreditorInstitution getMockStationCreditorInstitutionDetails() {
    return StationCreditorInstitution.builder()
        .applicationCode(1L)
        .auxDigit(1L)
        .segregationCode(80L)
        .mod4(false)
        .broadcast(false)
        .creditorInstitutionCode("1234")
        .enabled(true)
        .businessName("Comune di Roma")
        .build();
  }

  public static Encoding getMockEncoding() {
    return Encoding.builder()
        .paObjId(1L)
        .codeType(Encoding.CodeTypeEnum.QR_CODE)
        .encodingCode("12345678901")
        .codificheObjId(1L)
        .paObjId(2L)
        .build();
  }

  public static CreditorInstitutionEncodings getMockCreditorInstitutionEncodings() {
    return CreditorInstitutionEncodings.builder().encodings(List.of(getMockEncoding())).build();
  }

  public static Codifiche getMockCodifiche() {
    return Codifiche.builder().idCodifica("000111").objId(1L).build();
  }

  public static Psp getMockPsp() {
    return Psp.builder()
        .objId(1L)
        .abi("123")
        .bic("4321")
        .agidPsp(true)
        .codiceFiscale("AAABBB000X1A")
        .codiceMybank("7")
        .descrizione("Banca")
        .idPsp("50")
        .enabled(true)
        .fkIntQuadrature(IntermediariPsp.builder().objId(1L).build())
        .marcaBolloDigitale(true)
        .ragioneSociale("Poste Italiane")
        .vatNumber("123123123")
        .build();
  }

  public static CreditorInstitutionStationEdit getCreditorInstitutionStationEdit() {
    return CreditorInstitutionStationEdit.builder()
        .applicationCode(1L)
        .auxDigit(3L)
        .stationCode("80007580279_01")
        .broadcast(true)
        .segregationCode(5L)
        .fkPa(Pa.builder().objId(195L).build())
        .fkStazioni(Stazioni.builder().objId(40L).build())
        .build();
  }

  public static IntermediariPsp getMockIntermediariePsp() {
    return IntermediariPsp.builder()
        .objId(1L)
        .idIntermediarioPsp("1234")
        .codiceIntermediario("Regione Lazio")
        .enabled(true)
        .faultBeanEsteso(true)
        .build();
  }

  public static WfespPluginConf getMockWfespPluginConf() {
    return WfespPluginConf.builder()
        .id(1L)
        //                .idServPlugin("1")
        .idServPlugin("idServPlugin")
        .idBean("sdf")
        .build();
  }

  public static Canali getMockCanali() {
    return Canali.builder()
        .id(1L)
        .enabled(true)
        .descrizione("Canale")
        .fkCanaliNodo(getMockCanaliNodo())
        .idCanale("1234")
        .fkIntermediarioPsp(getMockIntermediariePsp())
        .ip("1.1.1.1")
        .password("pass")
        .protocollo("HTTP")
        .numThread(4L)
        .porta(80L)
        .proxyEnabled(false)
        .timeoutA(10L)
        .useNewFaultCode(true)
        .servizioNmp("NPM")
        .targetHost("localhost")
        .targetPort(443L)
        .targetPath("/")
        .targetHostNmp("localhost")
        .targetPortNmp(443L)
        .targetPathNmp("/")
        .build();
  }

  public static CanaliNodo getMockCanaliNodo() {
    return CanaliNodo.builder()
        .agidChannel(false)
        .id(1L)
        .carrelloCarte(true)
        .flagIo(true)
        .marcaBolloDigitale(true)
        .modelloPagamento("IMMEDIATO")
        .versionePrimitive(1)
        .build();
  }

  public static ElencoServizi getMockElencoServizi() {
    return ElencoServizi.builder()
        .canaleApp(true)
        .carrelloCarte(true)
        .codiceMybank("mybank")
        .id(1L)
        .canaleId("123")
        .flagIo(false)
        .canaleModPag(2L)
        .codiceLingua("IT")
        .tipoVersCod("PPAL")
        .nomeServizio("service")
        .timestampIns(Timestamp.valueOf("2021-12-13 00:00:00"))
        .dataValidita(Timestamp.valueOf("2021-12-14 00:00:00"))
        .build();
  }

  public static CdiMaster getMockCdiMaster() {
    return CdiMaster.builder()
        .id(1L)
        .fkPsp(getMockPsp())
        .fkBinaryFile(getMockBinaryFile())
        .cdiDetail(List.of(getMockCdiDetail()))
        .dataPubblicazione(Timestamp.valueOf("2021-12-14 00:00:00"))
        .dataInizioValidita(Timestamp.valueOf("2021-12-14 00:00:00"))
        .build();
  }

  public static CdiMasterValid getMockCdiMasterValid() {
    return CdiMasterValid.builder()
        .id(1L)
        .fkPsp(getMockPsp())
        .fkBinaryFile(getMockBinaryFile())
        .dataPubblicazione(Timestamp.valueOf("2021-12-14 00:00:00"))
        .dataInizioValidita(Timestamp.valueOf("2021-12-14 00:00:00"))
        .build();
  }

  public static CdiDetail getMockCdiDetail() {
    return CdiDetail.builder()
        .id(1L)
        .canaleApp(1L)
        .nomeServizio("service")
        .modelloPagamento(1L)
        .pspCanaleTipoVersamento(getMockPspCanaleTipoVersamento())
        .cdiInformazioniServizio(List.of(getMockCdiInformazioniServizio()))
        .cdiFasciaCostoServizio(List.of(getMockCdiFasciaCostoServizio()))
        .build();
  }

  public static CdiInformazioniServizio getMockCdiInformazioniServizio() {
    return CdiInformazioniServizio.builder()
        .id(1L)
        .codiceLingua("IT")
        .descrizioneServizio("descr")
        .build();
  }

  public static CdiFasciaCostoServizio getMockCdiFasciaCostoServizio() {
    return CdiFasciaCostoServizio.builder()
        .id(1L)
        .costoFisso(5.0)
        .valoreCommissione(5.0)
        .importoMinimo(1.0)
        .importoMassimo(10.0)
        .build();
  }

  public static CanaleTipoVersamento getMockCanaleTipoVersamento() {
    return CanaleTipoVersamento.builder()
        .id(1L)
        .canale(getMockCanali())
        .tipoVersamento(getMockTipoVersamento())
        .build();
  }

  public static TipiVersamento getMockTipoVersamento() {
    return TipiVersamento.builder().id(123L).tipoVersamento("PPAL").descrizione("PayPal").build();
  }

  public static List<TipiVersamento> getMockTipiVersamento() {
    return List.of(getMockTipoVersamento());
  }

  public static PspCanaleTipoVersamento getMockPspCanaleTipoVersamento() {
    return PspCanaleTipoVersamento.builder()
        .id(1L)
        .canaleTipoVersamento(getMockCanaleTipoVersamento())
        .psp(getMockPsp())
        .build();
  }

  public static BrokerPspDetails getMockBrokerPspDetails() {
    return BrokerPspDetails.builder()
        .brokerPspCode("1234")
        .enabled(true)
        .description("Regione Lazio")
        .extendedFaultBean(false)
        .build();
  }

  public static BrokersPsp getMockBrokersPsp() {
    return BrokersPsp.builder()
        .brokerPspList(List.of(getMockBrokerPspDetails()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static PaymentServiceProviderDetails getMockPaymentServiceProviderDetails() {
    return PaymentServiceProviderDetails.builder()
        .abi("abi")
        .enabled(true)
        .businessName("name")
        .agidPsp(true)
        .pspCode("1234ABC12345")
        .bic("435")
        .stamp(true)
        .vatNumber("123432")
        .myBankCode("mbank01")
        .taxCode("1")
        .build();
  }

  public static CreditorInstitution getMockCreditorInstitution() {
    return CreditorInstitution.builder()
        .enabled(true)
        .creditorInstitutionCode("1234")
        .businessName("regione")
        .build();
  }

  public static CreditorInstitutionView getMockCreditorInstitutionView() {
    return CreditorInstitutionView.builder()
        .idDominio("1234567890100")
        .idIntermediarioPa("223344556677889900")
        .idStazione("1234567890999")
        .auxDigit(1L)
        .progressivo(2L)
        .segregazione(3L)
        .build();
  }

  public static CreditorInstitutions getMockCreditorInstitutions() {
    return CreditorInstitutions.builder()
        .creditorInstitutionList(List.of(getMockCreditorInstitution()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static CreditorInstitutionsView getMockCreditorInstitutionsView() {
    return CreditorInstitutionsView.builder()
        .creditorInstitutionList(List.of(getMockCreditorInstitutionView()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static PageInfo getMockPageInfo() {
    return PageInfo.builder().page(0).limit(10).itemsFound(1).totalPages(1).build();
  }

  public static CreditorInstitutionStationList getMockCreditorInstitutionStationList() {
    return CreditorInstitutionStationList.builder()
        .stationsList(List.of(getMockCreditorInstitutionStation()))
        .build();
  }

  public static CreditorInstitutionStation getMockCreditorInstitutionStation() {
    return CreditorInstitutionStation.builder()
        .auxDigit(1L)
        .stationCode("1234")
        .enabled(true)
        .build();
  }

  public static Services getMockServices() {
    return Services.builder()
        .servicesList(List.of(getMockService()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static Service getMockService() {
    return Service.builder()
        .abiCode("12345")
        .pspCode("1234")
        .brokerPspCode("1234")
        .cartCard(true)
        .channelApp(false)
        .channelCode("aaaaa")
        .build();
  }

  public static Ibans getMockIbans() {
    return Ibans.builder().ibanList(List.of(getMockIban())).build();
  }

  public static IbansEnhanced getMockIbansEnhanced(
      OffsetDateTime validityDate, OffsetDateTime dueDate) {
    return IbansEnhanced.builder()
        .ibanEnhancedList(List.of(getMockIbanEnhanced(validityDate, dueDate)))
        .build();
  }

  public static Iban getMockIban() {
    return Iban.builder()
        .ibanValue("IT99C0222211111000000000000")
        .publicationDate(OffsetDateTime.now())
        .validityDate(OffsetDateTime.now())
        .build();
  }

  public static CreditorInstitutionStationEdit getMockCreditorInstitutionStationEdit() {
    return CreditorInstitutionStationEdit.builder().stationCode("12344").build();
  }

  public static PaymentServiceProviders getMockPaymentServiceProviders() {
    return PaymentServiceProviders.builder()
        .pageInfo(getMockPageInfo())
        .paymentServiceProviderList(List.of(getMockPaymentServiceProvider()))
        .build();
  }

  public static PaymentServiceProvider getMockPaymentServiceProvider() {
    return PaymentServiceProvider.builder()
        .businessName("ciao")
        .enabled(true)
        .pspCode("12345ABC12345")
        .taxCode("17103880000")
        .build();
  }

  public static PaymentServiceProviderView getMockPaymentServiceProviderView() {
    return PaymentServiceProviderView.builder()
        .pspCode("BPPIITRRHHH")
        .brokerPspCode("171038877777")
        .channelCode("00001060966_77")
        .paymentType("OBEP")
        .paymentMethod("PAYPAL")
        .build();
  }

  public static PaymentServiceProvidersView getMockPaymentServiceProvidersView() {
    return PaymentServiceProvidersView.builder()
        .pageInfo(getMockPageInfo())
        .paymentServiceProviderList(List.of(getMockPaymentServiceProviderView()))
        .build();
  }

  public static PspChannelList getMockPspChannelList() {
    return PspChannelList.builder().channelsList(List.of(getMockPspChannel())).build();
  }

  public static PspChannel getMockPspChannel() {
    return PspChannel.builder()
        .enabled(true)
        .channelCode("1234")
        .paymentTypeList(List.of("AD"))
        .build();
  }

  public static PspChannelCode getMockPspChannelCode() {
    return PspChannelCode.builder().channelCode("1234").paymentTypeList(List.of("AD")).build();
  }

  public static PspChannelPaymentTypes getMockPspChannelPaymentTypes() {
    return PspChannelPaymentTypes.builder().paymentTypeList(List.of("AD")).build();
  }

  public static ChannelPspList getChannelPspList() {
    return ChannelPspList.builder().psp(List.of(getChannelPsp())).build();
  }

  private static ChannelPsp getChannelPsp() {
    return ChannelPsp.builder()
        .enabled(true)
        .pspCode("1234")
        .businessName("Name")
        .paymentTypeList(List.of("AD"))
        .build();
  }

  public static Cdis getMockCdis() {
    return Cdis.builder().cdiList(List.of(getMockCdi())).pageInfo(getMockPageInfo()).build();
  }

  public static Cdi getMockCdi() {
    return Cdi.builder().idCdi("1233").pspCode("12321").businessName("ciao").build();
  }

  public static Channels getMockChannels() {
    return Channels.builder()
        .channelList(List.of(getMockChannelDetails()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static ChannelDetails getMockChannelDetails() {
    return ChannelDetails.builder()
        .brokerPspCode("1233")
        .channelCode("1")
        .enabled(true)
        .brokerDescription("channel")
        .timeoutA(1L)
        .timeoutB(1L)
        .timeoutC(1L)
        .threadNumber(2L)
        .password("***")
        .servPlugin("plugin")
        .protocol(Protocol.HTTP)
        .port(80L)
        .service("ABAB")
        .ip("1.1.1.1")
        .onUs(true)
        .cardChart(true)
        .recovery(true)
        .rtPush(true)
        .digitalStampBrand(false)
        .paymentModel(ChannelDetails.PaymentModel.DEFERRED)
        .agid(true)
        .flagPspCp(false)
        .primitiveVersion(1)
        .build();
  }

  public static CounterpartTables getMockCounterpartTables() {
    return CounterpartTables.builder()
        .counterpartTableList(List.of(getMockCounterpartTable()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static CounterpartTable getMockCounterpartTable() {
    return CounterpartTable.builder()
        .idCounterpartTable("1")
        .businessName("cc")
        .creditorInstitutionCode("1234")
        .publicationDate(OffsetDateTime.now())
        .validityDate(OffsetDateTime.now())
        .build();
  }

  public static Icas getMockIcas() {
    return Icas.builder().icaList(List.of(getMockIca())).pageInfo(getMockPageInfo()).build();
  }

  private static Ica getMockIca() {
    return Ica.builder()
        .businessName("sdfa")
        .idIca("12")
        .creditorInstitutionCode("123")
        .validityDate(OffsetDateTime.now())
        .publicationDate(OffsetDateTime.now())
        .build();
  }

  public static XSDValidation getMockXSDValidation() {
    return XSDValidation.builder().detail("detail").xsdCompliant(true).xsdSchema("schema").build();
  }

  public static ConfigurationKey getMockConfigurationKey() {
    return ConfigurationKey.builder()
        .configCategory("category")
        .configKey("key")
        .configValue("value")
        .configDescription("description")
        .build();
  }

  public static ConfigurationKey getMockConfigurationKey(String category, String key) {
    return getMockConfigurationKey();
  }

  public static ConfigurationKeys getMockConfigurationKeys() {
    return ConfigurationKeys.builder()
        .configurationKeyList(List.of(getMockConfigurationKey()))
        .build();
  }

  public static it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys
      getMockConfigurationKeyEntity() {
    return it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys.builder()
        .configCategory("category")
        .configKey("key")
        .configValue("value")
        .configDescription("description")
        .build();
  }

  public static List<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys>
      getMockConfigurationKeysEntries() {
    return List.of(getMockConfigurationKeyEntity());
  }

  public static FilterAndOrder getMockFilterAndOrder(OrderType orderBy) {
    return FilterAndOrder.builder()
        .order(Order.builder().orderBy(orderBy).ordering(Sort.Direction.DESC).build())
        .filter(Filter.builder().code("hello").build())
        .build();
  }

  public static it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf
      getMockModelWfespPluginConf() {
    return it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf.builder()
        .idServPlugin("idServPlugin")
        .idBean("sdf")
        .build();
  }

  public static WfespPluginConfs getMockWfespPluginConfigurations() {
    return WfespPluginConfs.builder()
        .wfespPluginConfList(List.of(getMockModelWfespPluginConf()))
        .build();
  }

  public static List<WfespPluginConf> getMockWfespPluginConfEntries() {
    return List.of(getMockWfespPluginConf());
  }

  public static Pdd getMockPdd() {
    return Pdd.builder()
        .idPdd("idPdd")
        .description("description")
        .enabled(true)
        .ip("127.0.0.1")
        .build();
  }

  public static Pdds getMockPdds() {
    return Pdds.builder().pddList(List.of(getMockPdd())).build();
  }

  public static it.gov.pagopa.apiconfig.starter.entity.Pdd getMockPddEntity() {
    return it.gov.pagopa.apiconfig.starter.entity.Pdd.builder()
        .idPdd("idPdd")
        .descrizione("description")
        .enabled(true)
        .ip("127.0.0.1")
        .build();
  }

  public static List<it.gov.pagopa.apiconfig.starter.entity.Pdd> getMockPddsEntities() {
    return List.of(getMockPddEntity());
  }

  public static FtpServer getMockFtpServer() {
    return FtpServer.builder()
        .host("host")
        .port(1)
        .service("service")
        .username("username")
        .password("pwd")
        .rootPath("/")
        .type("out")
        .enabled(true)
        .build();
  }

  public static FtpServers getMockFtpServers() {
    return FtpServers.builder().ftpServerList(List.of(getMockFtpServer())).build();
  }

  public static it.gov.pagopa.apiconfig.starter.entity.FtpServers getMockFtpServersEntity() {
    return it.gov.pagopa.apiconfig.starter.entity.FtpServers.builder()
        .host("host")
        .port(1)
        .service("service")
        .username("username")
        .password("pwd")
        .rootPath("/")
        .type("out")
        .enabled(true)
        .build();
  }

  public static List<it.gov.pagopa.apiconfig.starter.entity.FtpServers>
      getMockFtpServersEntities() {
    return List.of(getMockFtpServersEntity());
  }

  public static PaymentType getMockPaymentType() {
    return PaymentType.builder().paymentTypeCode("CODE").description("description").build();
  }

  public static PaymentTypes getMockPaymentTypes() {
    return PaymentTypes.builder().paymentTypeList(List.of(getMockPaymentType())).build();
  }

  public static AfmMarketplacePaymentType getMockAfmMarketplacePaymentType() {
    return AfmMarketplacePaymentType.builder().name("PPAL").used(false).build();
  }

  public static CacheVersions getMockCacheVersions() {
    return CacheVersions.builder()
        .versionList(List.of(Cache.builder().id("2023-02-08 01:00:06").version("3.10.0").build()))
        .pageInfo(getMockPageInfo())
        .build();
  }

  public static Page<Cache> getMockCacheVersionsPaged() {
    return mockPage(
        List.of(Cache.builder().id("2023-02-08 01:00:06").version("3.10.0").build()), 50, 0);
  }

  public static Optional<Cache> getMockCacheByVersion() {
    return Optional.of(getMockCache());
  }

  public static Cache getMockCache() {
    return Cache.builder()
        .id("2023-02-08 01:00:06")
        .cache(new byte[] {1, 11})
        .version("3.10.0")
        .build();
  }

  public static Page<CdiMasterValid> getMockCdiMasterPaged() {
    return mockPage(List.of(getMockCdiMasterValid()), 1, 0);
  }

  public static IbanEnhanced getMockIbanEnhanced(
      OffsetDateTime validityDate, OffsetDateTime dueDate) {
    return mockIbanEnhancedBuilder("IT99C0222211111000000000000", validityDate, dueDate);
  }

  public static IbanEnhanced getMockPostalIbanEnhanced(
      OffsetDateTime validityDate, OffsetDateTime dueDate) {
    return mockIbanEnhancedBuilder("IT99C0760111111000000000000", validityDate, dueDate);
  }

  private static IbanEnhanced mockIbanEnhancedBuilder(
      String ibanValue, OffsetDateTime validityDate, OffsetDateTime dueDate) {
    return IbanEnhanced.builder()
        .ibanValue(ibanValue)
        .description("Riscossione tributi")
        .isActive(true)
        .validityDate(validityDate)
        .dueDate(dueDate)
        .labels(
            List.of(
                IbanLabel.builder()
                    .name("CUP")
                    .description("The iban to use for CUP payments")
                    .build(),
                IbanLabel.builder()
                    .name("STANDIN")
                    .description("The iban to use for ACA/Standin payments")
                    .build()))
        .build();
  }

  public static IcaBinaryFile getMockIcaBinaryFile() {
    return IcaBinaryFile.builder()
        .objId(10L)
        .idDominio("00168480242")
        .fileContent(new byte[] {1, 10, 20, 30, 40})
        .fileHash(new byte[] {100, 100})
        .fileSize(5L)
        .build();
  }

  public static IcaBinaryFile getMockIcaBinaryFile_2() {
    return IcaBinaryFile.builder()
        .objId(11L)
        .idDominio("00168480243")
        .fileContent(new byte[] {1, 10, 20, 30, 40})
        .fileHash(new byte[] {100, 100})
        .fileSize(5L)
        .build();
  }

  public static List<IbanAttribute> getMockIbanAttributes() {
    return List.of(
        IbanAttribute.builder()
            .attributeName("CUP")
            .attributeDescription("The iban to use for CUP payments")
            .build(),
        IbanAttribute.builder()
            .attributeName("STANDIN")
            .attributeDescription("The iban to use for ACA/Standin payments")
            .build());
  }

  public static IbanEnhanced getMockIbanEnhanced_2() {
    return IbanEnhanced.builder()
        .ibanValue("IT99C0222211111000000000003")
        .publicationDate(OffsetDateTime.now())
        .validityDate(OffsetDateTime.now())
        .build();
  }

  public static it.gov.pagopa.apiconfig.starter.entity.Iban getMockIbanEntity(String ibanCode) {
    return it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
        .objId(1L)
        .iban(ibanCode)
        .fiscalCode("1234")
        .description("Iban description")
        .build();
  }

  public static IbanMaster getMockIbanMaster_2() {
    return IbanMaster.builder()
        .objId(1L)
        .insertedDate(Timestamp.valueOf("2021-10-01 17:48:22"))
        .validityDate(Timestamp.valueOf("2021-10-01 17:48:22"))
        .build();
  }

  public static IbanAttributeMaster getMockIbanAttributeMaster() {
    return IbanAttributeMaster.builder().build();
  }

  public static List<IbanAttributeMaster> getMockIbanAttributeMasters(IbanMaster ibanMaster) {
    return List.of(
        IbanAttributeMaster.builder()
            .ibanAttribute(
                IbanAttribute.builder()
                    .attributeName("STANDIN")
                    .attributeDescription("The iban to use for ACA/Standin payments")
                    .build())
            .ibanMaster(ibanMaster)
            .build());
  }

  public static List<IbanMaster> getMockIbanMasters(
          Pa creditorInstitution, IbanEnhanced iban, it.gov.pagopa.apiconfig.starter.entity.Iban ibanEntity) {
      List<IbanMaster> ibanMasters =
              List.of(
                      IbanMaster.builder()
                              .objId(100L)
                              .fkPa(creditorInstitution.getObjId())
                              .fkIban(ibanEntity.getObjId())
                              .ibanStatus(iban.isActive() ? IbanMaster.IbanStatus.ENABLED : IbanMaster.IbanStatus.DISABLED)
                              .insertedDate(
                                      CommonUtil.toTimestamp(OffsetDateTime.parse("2023-05-23T10:38:07.165+02")))
                              .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
                              .description(iban.getDescription())
                              .build());
      ibanMasters.get(0).setIbanAttributesMasters(getMockIbanAttributeMasters(ibanMasters.get(0)));
      return ibanMasters;
  }

  public it.gov.pagopa.apiconfig.starter.entity.Iban getMockIban(IbanEnhanced iban, String organizationFiscalCode) {
      return it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
              .objId(100L)
              .iban(iban.getIbanValue())
              .fiscalCode(organizationFiscalCode)
              .description("iban")
              .dueDate(CommonUtil.toTimestamp(iban.getDueDate()))
              .build();
  }

  public IbanMaster getMockIbanMaster(
          Pa creditorInstitution, IbanEnhanced iban, it.gov.pagopa.apiconfig.starter.entity.Iban ibanToBeCreated) {
      return IbanMaster.builder()
              .objId(100L)
              .fkPa(creditorInstitution.getObjId())
              .pa(creditorInstitution)
              .fkIban(ibanToBeCreated.getObjId())
              .iban(ibanToBeCreated)
              .ibanStatus(iban.isActive() ? IbanMaster.IbanStatus.ENABLED : IbanMaster.IbanStatus.DISABLED)
              .insertedDate(CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)))
              .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
              .description(iban.getDescription())
              .build();
  }

  public it.gov.pagopa.apiconfig.starter.entity.Iban getMockIban(String organizationFiscalCode) {
      return it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
              .objId(100L)
              .iban("IT99C0222211111000000000004")
              .fiscalCode(organizationFiscalCode)
              .description("iban")
              .dueDate(Timestamp.valueOf(LocalDateTime.now().plusYears(3)))
              .build();
    }
  public IbanMaster getMockIbanMasterValidityDateInsertedDate(
          Pa creditorInstitution, it.gov.pagopa.apiconfig.starter.entity.Iban ibanToBeCreated, Timestamp validityDate, Timestamp insertedDate) {
      return IbanMaster.builder()
              .objId(100L)
              .fkPa(creditorInstitution.getObjId())
              .pa(creditorInstitution)
              .fkIban(ibanToBeCreated.getObjId())
              .iban(ibanToBeCreated)
              .ibanStatus(IbanMaster.IbanStatus.ENABLED)
              .insertedDate(insertedDate)
              .validityDate(validityDate)
              .description(ibanToBeCreated.getDescription())
              .build();
  }
}
