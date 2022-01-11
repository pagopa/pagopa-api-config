package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelList;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
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
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviderDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPsp;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPspCanaleTipoVersamento;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class PspServiceTest {

    @MockBean
    PspRepository pspRepository;

    @MockBean
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    @InjectMocks
    @Autowired
    PspService pspService;

    @Test
    void getPaymentServiceProviders() throws IOException, JSONException {
        Page<Psp> page = TestUtil.mockPage(Lists.newArrayList(getMockPsp()), 50, 0);
        when(pspRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaymentServiceProviders result = pspService.getPaymentServiceProviders(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_paymentserviceproviders_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getPaymentServiceProvider() throws IOException, JSONException {
        when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));

        PaymentServiceProviderDetails result = pspService.getPaymentServiceProvider("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_paymentserviceprovider_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getPaymentServiceProvider_notFound() {
        when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.empty());

        try {
            pspService.getPaymentServiceProvider("1234");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    void createPsp() throws IOException, JSONException {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.empty());
        when(pspRepository.save(any(Psp.class))).thenReturn(getMockPsp());

        var result = pspService.createPaymentServiceProvider(getMockPaymentServiceProviderDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_psp_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createPsp_conflict() {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.of(getMockPsp()));

        try {
            pspService.createPaymentServiceProvider(getMockPaymentServiceProviderDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updatePsp() throws IOException, JSONException {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.of(getMockPsp()));
        when(pspRepository.save(any(Psp.class))).thenReturn(getMockPsp());

        var result = pspService.updatePaymentServiceProvider("1234", getMockPaymentServiceProviderDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_psp_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updatePsp_notFound() {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.empty());
        try {
            pspService.updatePaymentServiceProvider("1234", getMockPaymentServiceProviderDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePsp() {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.of(getMockPsp()));

        pspService.deletePaymentServiceProvider("1234");
        assertTrue(true);
    }

    @Test
    void deletePsp_notfound() {
        when(pspRepository.findByIdPsp("1234")).thenReturn(Optional.empty());

        try {
            pspService.deletePaymentServiceProvider("1234");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void getPaymentServiceProvidersChannels() throws IOException, JSONException {
        when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
        when(pspCanaleTipoVersamentoRepository.findByFkPsp(anyLong())).thenReturn(Lists.newArrayList(getMockPspCanaleTipoVersamento()));

        PspChannelList result = pspService.getPaymentServiceProvidersChannels("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_psp_channels_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}
