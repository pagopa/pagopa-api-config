package it.pagopa.pagopa.apiconfig;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pagopa.apiconfig.entity.BinaryFile;
import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
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
                .fkIntermediarioPa(IntermediariPa.builder().objId(2L).build())
                .redirectProtocollo("HTTPS")
                .proxyEnabled(true)
                .proxyHost("10.101.1.95")
                .proxyPort(8080L)
                .numThread(2L)
                .timeoutA(15L)
                .timeoutB(30L)
                .timeoutC(120L)
                .flagOnline(true)
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
                .fkPa(getMockPa())
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
                .fkCodifica(Codifiche.builder()
                        .id(2L)
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
                .codiceIntermediario("1234")
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

    private static BinaryFile getMockBinaryFile() {
        return BinaryFile.builder()
                .fileContent(new byte[]{1, 11})
                .build();
    }

    public static InformativePaMaster getMockInformativePaMaster() {
        return InformativePaMaster.builder()
                .idInformativaPa("111")
                .fkPa(getMockPa())
                .fkBinaryFile(getMockBinaryFile())
                .dataInizioValidita(Timestamp.valueOf("2017-03-09 00:00:00"))
                .dataPubblicazione(Timestamp.valueOf("2017-03-09 00:00:00"))
                .build();
    }

    public static CreditorInstitutionDetails getMockCreditorInstitutionDetails(){
        return CreditorInstitutionDetails.builder()
                .pspPayment(false)
                .fkQuadrature(0L)
                .creditorInstitutionCode("1234")
                .businessName("Città di Roma")
                .enabled(true)
                .address(CreditorInstitutionAddress.builder()
                        .city("Roma")
                        .build())
                .build();
    }


}
