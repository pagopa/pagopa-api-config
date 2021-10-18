package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockStationDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void createStation() throws IOException, JSONException {
        when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.empty());
        when(stazioniRepository.save(any(Stazioni.class))).thenReturn(getMockStazioni());

        StationDetails result = stationsService.createStation(getMockStationDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_station_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateStation() throws IOException, JSONException {
        when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
        when(stazioniRepository.save(any(Stazioni.class))).thenReturn(getMockStazioni());

        StationDetails result = stationsService.updateStation("1234", getMockStationDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_station_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void deleteStation() {
        when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));

        stationsService.deleteStation("1234");
        assertTrue(true);
    }

}
