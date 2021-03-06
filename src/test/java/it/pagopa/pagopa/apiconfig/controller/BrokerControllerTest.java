package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.service.BrokersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBrokerDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBrokers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
class BrokerControllerTest {

    @Autowired
    private MockMvc mvc;


    @MockBean
    private BrokersService brokersService;

    @BeforeEach
    void setUp() {
        when(brokersService.getBrokers(anyInt(), anyInt(), any(FilterAndOrder.class))).thenReturn(getMockBrokers());
        when(brokersService.getBroker(anyString())).thenReturn(getMockBrokerDetails());
        when(brokersService.createBroker(any(BrokerDetails.class))).thenReturn(getMockBrokerDetails());
        when(brokersService.updateBroker(anyString(), any(BrokerDetails.class))).thenReturn(getMockBrokerDetails());
    }

    @Test
    void getBrokers() throws Exception {
        String url = "/brokers?page=0";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBroker() throws Exception {
        String url = "/brokers/1234";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createBroker() throws Exception {
        mvc.perform(post("/brokers")
                        .content(TestUtil.toJson(getMockBrokerDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void createBroker_400() throws Exception {
        mvc.perform(post("/brokers")
                        .content(TestUtil.toJson(getMockBrokerDetails().toBuilder()
                                .brokerCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateBroker() throws Exception {
        mvc.perform(put("/brokers/1234")
                        .content(TestUtil.toJson(getMockBrokerDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateBroker_400() throws Exception {
        mvc.perform(put("/brokers/1234")
                        .content(TestUtil.toJson(getMockBrokerDetails().toBuilder()
                                .brokerCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteBroker() throws Exception {
        mvc.perform(delete("/brokers/1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
