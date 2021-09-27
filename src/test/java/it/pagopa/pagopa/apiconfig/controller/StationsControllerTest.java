package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.Stations;
import it.pagopa.pagopa.apiconfig.service.StationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
