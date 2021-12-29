package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Stations;
import it.pagopa.pagopa.apiconfig.service.StationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockStationDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class StationsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private StationsService stationsService;

    @BeforeEach
    void setUp() {
        when(stationsService.getStations(50, 0, null, null)).thenReturn(Stations.builder().build());
        when(stationsService.getStation(anyString())).thenReturn(StationDetails.builder().build());
        when(stationsService.createStation(any(StationDetails.class))).thenReturn(StationDetails.builder().build());
        when(stationsService.updateStation(anyString(), any(StationDetails.class))).thenReturn(StationDetails.builder().build());
    }

    @Test
    void getStation() throws Exception {
        String url = "/stations/1234";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStations() throws Exception {
        String url = "/stations?page=0";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createStation() throws Exception {
        mvc.perform(post("/stations")
                        .content(TestUtil.toJson(getMockStationDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void createStation_400() throws Exception {
        mvc.perform(post("/stations")
                        .content(TestUtil.toJson(getMockStationDetails().toBuilder()
                                .stationCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateStation() throws Exception {
        mvc.perform(put("/stations/1234")
                        .content(TestUtil.toJson(getMockStationDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteStation() throws Exception {
        mvc.perform(delete("/stations/1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
