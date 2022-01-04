package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelList;
import it.pagopa.pagopa.apiconfig.service.PspService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
class PspControllerTest {

    @MockBean
    PspService pspService;
    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        when(pspService.getPaymentServiceProviders(50, 0)).thenReturn(PaymentServiceProviders.builder().build());
        when(pspService.getPaymentServiceProvider(anyString())).thenReturn(PaymentServiceProviderDetails.builder().build());
        when(pspService.getPaymentServiceProvidersChannels(anyString())).thenReturn(PspChannelList.builder().build());
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
}
