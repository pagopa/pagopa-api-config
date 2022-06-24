package it.pagopa.pagopa.apiconfig;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.CanaliNodo;
import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.ElencoServizi;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.entity.InformativePaDetail;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.model.PageInfo;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServer;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServers;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentTypes;
import it.pagopa.pagopa.apiconfig.model.configuration.Pdd;
import it.pagopa.pagopa.apiconfig.model.configuration.Pdds;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Brokers;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Iban;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ibans;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationCreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Stations;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.XSDValidation;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Filter;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.filterandorder.OrderType;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.model.psp.BrokersPsp;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.model.psp.Cdis;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelPsp;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelPspList;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannel;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelCode;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelList;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
import it.pagopa.pagopa.apiconfig.model.psp.Services;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@UtilityClass
public class TestUtil {

    /**
     * @param relativePath Path from source root of the json file
     * @return the Json string read from the file
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
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
     * @param content    list of elements in the page to return by mock
     * @param limit      number of elements per page to return by mock
     * @param pageNumber number of page to return by mock
     * @param <T>        Class of the elements
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
                .build();
    }


    public static Pa getMockPa() {
        return Pa.builder()
                .objId(1L)
                .idDominio("00168480242")
                .enabled(true)
                .ragioneSociale("Comune di Bassano del Grappa")
                .capDomicilioFiscale(123L)
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
                .stazioneAvv(false)
                .stazioneNodo(false)
                .build();
    }

    public static CodifichePa getMockCodifichePa() {
        return CodifichePa.builder()
                .id(1L)
                .codicePa("1234")
                .fkCodifica(Codifiche.builder()
                        .objId(2L)
                        .idCodifica("QR-CODE")
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
        return BinaryFile.builder()
                .id(1L)
                .fileSize(2L)
                .fileContent(new byte[]{1, 11})
                .build();
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
                .address(CreditorInstitutionAddress.builder()
                        .city("Roma")
                        .zipCode("00111")
                        .build())
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
                .protocol("http")
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
        return CreditorInstitutionEncodings.builder()
                .encodings(List.of(getMockEncoding()))
                .build();
    }

    public static Codifiche getMockCodifiche() {
        return Codifiche.builder()
                .idCodifica("000111")
                .objId(1L)
                .build();
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
                .fkIntQuadrature(IntermediariPsp.builder()
                        .objId(1L)
                        .build())
                .pspAvv(true)
                .emailRepoCommissioneCaricoPa("email@email.com")
                .flagRepoCommissioneCaricoPa(false)
                .pspNodo(true)
                .marcaBolloDigitale(true)
                .ragioneSociale("Poste Italiane")
                .stornoPagamento(false)
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
                .canaleAvv(false)
                .canaleNodo(true)
                .fkCanaliNodo(getMockCanaliNodo())
                .idCanale("1234")
                .fkIntermediarioPsp(getMockIntermediariePsp())
                .ip("1.1.1.1")
                .newPassword("new-pass")
                .password("pass")
                .protocollo("HTTP")
                .numThread(4L)
                .porta(80L)
                .proxyEnabled(false)
                .timeoutA(10L)
                .useNewFaultCode(true)
                .servizioNmp("NPM")
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
                .dataPubblicazione(Timestamp.valueOf("2021-12-14 00:00:00"))
                .dataInizioValidita(Timestamp.valueOf("2021-12-14 00:00:00"))
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
        return TipiVersamento.builder()
                .id(123L)
                .tipoVersamento("PPAL")
                .descrizione("PayPal")
                .build();
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
                .pspCode("1234")
                .bic("435")
                .stamp(true)
                .transfer(false)
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

    public static CreditorInstitutions getMockCreditorInstitutions() {
        return CreditorInstitutions.builder()
                .creditorInstitutionList(List.of(getMockCreditorInstitution()))
                .pageInfo(getMockPageInfo())
                .build();
    }

    public static PageInfo getMockPageInfo() {
        return PageInfo.builder()
                .page(0)
                .limit(10)
                .itemsFound(1)
                .totalPages(1)
                .build();
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
        return Ibans.builder()
                .ibanList(List.of(getMockIban()))
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
        return CreditorInstitutionStationEdit.builder()
                .stationCode("12344")
                .build();
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
                .pspCode("1")
                .build();
    }


    public static PspChannelList getMockPspChannelList() {
        return PspChannelList.builder()
                .channelsList(List.of(getMockPspChannel()))
                .build();
    }

    public static PspChannel getMockPspChannel() {
        return PspChannel.builder()
                .enabled(true)
                .channelCode("1234")
                .paymentTypeList(List.of("AD"))
                .build();
    }

    public static PspChannelCode getMockPspChannelCode() {
        return PspChannelCode.builder()
                .channelCode("1234")
                .paymentTypeList(List.of("AD"))
                .build();
    }

    public static PspChannelPaymentTypes getMockPspChannelPaymentTypes() {
        return PspChannelPaymentTypes.builder()
                .paymentTypeList(List.of("AD"))
                .build();
    }

    public static ChannelPspList getChannelPspList() {
        return ChannelPspList.builder()
                .psp(List.of(getChannelPsp()))
                .build();
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
        return Cdis.builder()
                .cdiList(List.of(getMockCdi()))
                .pageInfo(getMockPageInfo())
                .build();
    }

    public static Cdi getMockCdi() {
        return Cdi.builder()
                .idCdi("1233")
                .pspCode("12321")
                .businessName("ciao")
                .build();
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
                .description("channel")
                .timeoutA(1L)
                .timeoutB(1L)
                .timeoutC(1L)
                .threadNumber(2L)
                .password("***")
                .servPlugin("plugin")
                .protocol("HTTP")
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
        return Icas.builder()
                .icaList(List.of(getMockIca()))
                .pageInfo(getMockPageInfo())
                .build();
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
        return XSDValidation.builder()
                .detail("detail")
                .xsdCompliant(true)
                .xsdSchema("schema")
                .build();
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

    public static it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys getMockConfigurationKeyEntity() {
        return it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.builder()
                .configCategory("category")
                .configKey("key")
                .configValue("value")
                .configDescription("description")
                .build();
    }

    public static List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> getMockConfigurationKeysEntries() {
        return List.of(getMockConfigurationKeyEntity());
    }

    public static FilterAndOrder getMockFilterAndOrder(OrderType orderBy) {
        return FilterAndOrder.builder()
                .order(Order.builder()
                        .orderBy(orderBy)
                        .ordering(Sort.Direction.DESC)
                        .build())
                .filter(Filter.builder()
                        .code("hello")
                        .build())
                .build();
    }

    public static it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf getMockModelWfespPluginConf() {
        return it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf.builder()
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
        return Pdds.builder()
                .pddList(List.of(getMockPdd()))
                .build();
    }

    public static it.pagopa.pagopa.apiconfig.entity.Pdd getMockPddEntity() {
        return it.pagopa.pagopa.apiconfig.entity.Pdd.builder()
                .idPdd("idPdd")
                .descrizione("description")
                .enabled(true)
                .ip("127.0.0.1")
                .build();
    }

    public static List<it.pagopa.pagopa.apiconfig.entity.Pdd> getMockPddsEntities() {
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
        return FtpServers.builder()
                .ftpServerList(List.of(getMockFtpServer()))
                .build();
    }

    public static it.pagopa.pagopa.apiconfig.entity.FtpServers getMockFtpServersEntity() {
        return it.pagopa.pagopa.apiconfig.entity.FtpServers.builder()
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

    public static List<it.pagopa.pagopa.apiconfig.entity.FtpServers> getMockFtpServersEntities() {
        return List.of(getMockFtpServersEntity());
    }

    public static PaymentType getMockPaymentType() {
        return PaymentType.builder()
                .paymentTypeCode("code")
                .description("description")
                .build();
    }

    public static PaymentTypes getMockPaymentTypes() {
        return PaymentTypes.builder()
                .paymentTypeList(List.of(getMockPaymentType()))
                .build();
    }
    

}
