package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviderDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviders;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaymentServiceProvidersView;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspChannel;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspChannelList;
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
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelCode;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.core.service.PspService;
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
class PspControllerTest {

  @MockBean PspService pspService;
  @Autowired private MockMvc mvc;

  @BeforeEach
  void setUp() {
    when(pspService.getPaymentServiceProviders(anyInt(), anyInt(), any(FilterAndOrder.class)))
        .thenReturn(getMockPaymentServiceProviders());
    when(pspService.getPaymentServiceProvider(anyString()))
        .thenReturn(getMockPaymentServiceProviderDetails());
    when(pspService.getPaymentServiceProvidersChannels(anyString()))
        .thenReturn(getMockPspChannelList());
    when(pspService.createPaymentServiceProvider(any(PaymentServiceProviderDetails.class)))
        .thenReturn(getMockPaymentServiceProviderDetails());
    when(pspService.updatePaymentServiceProvider(
            anyString(), any(PaymentServiceProviderDetails.class)))
        .thenReturn(getMockPaymentServiceProviderDetails());
    when(pspService.createPaymentServiceProvidersChannels(anyString(), any(PspChannelCode.class)))
        .thenReturn(getMockPspChannel());
    when(pspService.updatePaymentServiceProvidersChannels(
            anyString(), anyString(), any(PspChannelPaymentTypes.class)))
        .thenReturn(getMockPspChannel());
    when(pspService.getPaymentServiceProvidersView(
            anyInt(), anyInt(), any(), any(), any(), any(), any()))
        .thenReturn(getMockPaymentServiceProvidersView());
  }

  @ParameterizedTest
  @CsvSource({
    "/paymentserviceproviders?page=0",
    "/paymentserviceproviders/1234ABC12345",
    "/paymentserviceproviders/1234ABC12345/channels",
    "/paymentserviceproviders/view?page=0",
    "/paymentserviceproviders/view?page=0&pspCode=BPPIITRRHHH",
    "/paymentserviceproviders/view?page=0&pspCode=BPPIITRRHHH&taxCode=AAABBB000X1A"
  })
  void getPsp(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createPsp() throws Exception {
    mvc.perform(
            post("/paymentserviceproviders")
                .content(TestUtil.toJson(getMockPaymentServiceProviderDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createPsp_400() throws Exception {
    mvc.perform(
            post("/paymentserviceproviders")
                .content(
                    TestUtil.toJson(
                        getMockPaymentServiceProviderDetails().toBuilder().pspCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updatePsp() throws Exception {
    mvc.perform(
            put("/paymentserviceproviders/1234ABC12345")
                .content(TestUtil.toJson(getMockPaymentServiceProviderDetails()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deletePsp() throws Exception {
    mvc.perform(
            delete("/paymentserviceproviders/1234ABC12345").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void createPspChannel() throws Exception {
    mvc.perform(
            post("/paymentserviceproviders/1234ABC12345/channels")
                .content(TestUtil.toJson(getMockPspChannel()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updatePspChannel() throws Exception {
    mvc.perform(
            put("/paymentserviceproviders/1234ABC12345/channels/1234")
                .content(TestUtil.toJson(getMockPspChannel()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deletePspChannel() throws Exception {
    mvc.perform(delete("/paymentserviceproviders/1234ABC12345/channels/1234"))
        .andExpect(status().isOk());
  }
}
