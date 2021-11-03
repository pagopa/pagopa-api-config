package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;
import it.pagopa.pagopa.apiconfig.service.CounterpartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class CounterpartControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CounterpartService counterpartService;

    @BeforeEach
    void setUp() {
        when(counterpartService.getCounterpartTables(50, 0)).thenReturn(CounterpartTables.builder().build());
        when(counterpartService.getCounterpartTable(anyString(), anyString())).thenReturn(new byte[]{});
    }

    @Test
    void getCounterpartTables() throws Exception {
        String url = "/counterparttables?page=0";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCounterpartTable() throws Exception {
        String url = "/counterparttables/123?creditorinstitutioncode=111";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }
}
