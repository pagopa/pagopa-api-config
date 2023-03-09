package it.pagopa.pagopa.apiconfig.controller;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBrokerPspDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBrokersPsp;
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

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.service.BrokersPspService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class BrokerPspControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private BrokersPspService brokersPspService;

  @BeforeEach
  void setUp() {
    when(brokersPspService.getBrokersPsp(anyInt(), anyInt(), any(FilterAndOrder.class)))
        .thenReturn(getMockBrokersPsp());
    when(brokersPspService.getBrokerPsp(anyString())).thenReturn(getMockBrokerPspDetails());
    when(brokersPspService.createBrokerPsp(any(BrokerPspDetails.class)))
        .thenReturn(getMockBrokerPspDetails());
    when(brokersPspService.updateBrokerPsp(anyString(), any(BrokerPspDetails.class)))
        .thenReturn(getMockBrokerPspDetails());
  }

  @Test
  void getBrokersPsp() throws Exception {
    String url = "/brokerspsp?page=0";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getBrokerPsp() throws Exception {
    String url = "/brokerspsp/1234";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createBrokerPsp() throws Exception {
    mvc.perform(
            post("/brokerspsp")
                .content(TestUtil.toJson(getMockBrokerPspDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createBrokerPsp_400() throws Exception {
    mvc.perform(
            post("/brokerspsp")
                .content(
                    TestUtil.toJson(
                        getMockBrokerPspDetails().toBuilder().brokerPspCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateBrokerPsp() throws Exception {
    mvc.perform(
            put("/brokerspsp/1234")
                .content(TestUtil.toJson(getMockBrokerPspDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateBrokerPsp_400() throws Exception {
    mvc.perform(
            put("/brokerspsp/1234")
                .content(
                    TestUtil.toJson(
                        getMockBrokerPspDetails().toBuilder().brokerPspCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteBrokerPsp() throws Exception {
    mvc.perform(delete("/brokerspsp/1234").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getPspBrokerPsp() throws Exception {
    mvc.perform(
            get("/brokerspsp/1234/paymentserviceproviders?page=0")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
