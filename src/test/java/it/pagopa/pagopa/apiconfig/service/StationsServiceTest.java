package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import it.pagopa.pagopa.apiconfig.model.Stations;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class StationsServiceTest {

    @MockBean
    private StazioniRepository stazioniRepository;

    @Autowired
    @InjectMocks
    private StationsService stationsService;

    @Test
    void getStations() throws IOException, JSONException {
        Page<Stazioni> page = TestUtil.mockPage(Lists.newArrayList(getMockStazioni()), 50, 0);
        when(stazioniRepository.findAllFilterByIntermediarioAndPa(anyString(), anyString(), any(Pageable.class))).thenReturn(page);

        Stations result = stationsService.getStations(50, 0, "1234", "4321");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getStation() throws IOException, JSONException {
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));

        StationDetails result = stationsService.getStation("80007580279_01");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_station_ok1.json");
        System.out.println(actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    private Stazioni getMockStazioni() {
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
                .protocolloAvv("HTTP")
                .numThread(2L)
                .timeoutA(15L)
                .timeoutB(30L)
                .timeoutC(120L)
                .flagOnline(true)
                .build();
    }


}
