package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.service.PspService;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviderDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviders;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPspChannelList;
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
class PspControllerTest {

    @MockBean
    PspService pspService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        when(pspService.getPaymentServiceProviders(50, 0)).thenReturn(getMockPaymentServiceProviders());
        when(pspService.getPaymentServiceProvider(anyString())).thenReturn(getMockPaymentServiceProviderDetails());
        when(pspService.getPaymentServiceProvidersChannels(anyString())).thenReturn(getMockPspChannelList());
        when(pspService.createPaymentServiceProvider(any(PaymentServiceProviderDetails.class))).thenReturn(getMockPaymentServiceProviderDetails());
        when(pspService.updatePaymentServiceProvider(anyString(), any(PaymentServiceProviderDetails.class))).thenReturn(getMockPaymentServiceProviderDetails());
    }

    @ParameterizedTest
    @CsvSource({
            "/paymentserviceproviders?page=0",
            "/paymentserviceproviders/1234",
            "/paymentserviceproviders/1234/channels",
    })
    void getPsp(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createPsp() throws Exception {
        mvc.perform(post("/paymentserviceproviders")
                        .content(TestUtil.toJson(getMockPaymentServiceProviderDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    void createPsp_400() throws Exception {
        mvc.perform(post("/paymentserviceproviders")
                        .content(TestUtil.toJson(getMockPaymentServiceProviderDetails().toBuilder()
                                .pspCode("")
                                .build()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updatePsp() throws Exception {
        mvc.perform(put("/paymentserviceproviders/1234")
                        .content(TestUtil.toJson(getMockPaymentServiceProviderDetails()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deletePsp() throws Exception {
        mvc.perform(delete("/paymentserviceproviders/1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
