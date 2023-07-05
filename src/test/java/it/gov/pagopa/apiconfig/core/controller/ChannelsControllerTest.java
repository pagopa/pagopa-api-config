package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getChannelPspList;
import static it.gov.pagopa.apiconfig.TestUtil.getMockChannelDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockChannels;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspChannelPaymentTypes;
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

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.core.model.psp.ChannelDetails;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.core.service.ChannelsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class ChannelsControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private ChannelsService channelsService;

  @BeforeEach
  void setUp() {
    when(channelsService.getChannels(anyInt(), anyInt(), any(), any(), any(FilterAndOrder.class)))
        .thenReturn(getMockChannels());
    when(channelsService.getChannel(anyString())).thenReturn(getMockChannelDetails());
    when(channelsService.createChannel(any(ChannelDetails.class)))
        .thenReturn(getMockChannelDetails());
    when(channelsService.updateChannel(anyString(), any(ChannelDetails.class)))
        .thenReturn(getMockChannelDetails());

    when(channelsService.getPaymentTypes(anyString())).thenReturn(getMockPspChannelPaymentTypes());
    when(channelsService.createPaymentType(anyString(), any(PspChannelPaymentTypes.class)))
        .thenReturn(getMockPspChannelPaymentTypes());
    when(channelsService.getChannelPaymentServiceProviders(
            anyInt(), anyInt(), anyString(), any(), any(), any()))
        .thenReturn(getChannelPspList());
    when(channelsService.getChannelPaymentServiceProvidersCSV(anyString())).thenReturn(new byte[0]);
    when(channelsService.getChannelsCSV()).thenReturn(new byte[0]);
  }

  @ParameterizedTest
  @CsvSource({
    "/channels?page=0",
    "/channels/1234",
    "/channels/1234/paymenttypes",
    "/channels/1234/paymentserviceproviders?page=0"
  })
  void getChannels(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createChannel() throws Exception {
    mvc.perform(
            post("/channels")
                .content(TestUtil.toJson(getMockChannelDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createChannel_400() throws Exception {
    mvc.perform(
            post("/channels")
                .content(
                    TestUtil.toJson(getMockChannelDetails().toBuilder().brokerPspCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateChannel() throws Exception {
    mvc.perform(
            put("/channels/1234")
                .content(TestUtil.toJson(getMockChannelDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateChannel_400() throws Exception {
    mvc.perform(
            put("/channels/1234")
                .content(
                    TestUtil.toJson(getMockChannelDetails().toBuilder().brokerPspCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteChannel() throws Exception {
    mvc.perform(delete("/channels/1234").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createPaymentType() throws Exception {
    mvc.perform(
            post("/channels/1234/paymenttypes")
                .content(TestUtil.toJson(getMockPspChannelPaymentTypes()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deletePaymentType() throws Exception {
    mvc.perform(delete("/channels/1234/paymenttypes/PO").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getPspCsv() throws Exception {
    String url = "/channels/1234/paymentserviceproviders/csv";
    mvc.perform(get(url).contentType(MediaType.TEXT_PLAIN_VALUE)).andExpect(status().isOk());
  }

  @Test
  void getChannelsCsv() throws Exception {
    String url = "/channels/csv";
    mvc.perform(get(url).contentType(MediaType.TEXT_PLAIN_VALUE)).andExpect(status().isOk());
  }
}
