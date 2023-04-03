package it.gov.pagopa.apiconfig.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCanaleTipoVersamento;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCanali;
import static it.gov.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaymentServiceProviderDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPsp;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspCanaleTipoVersamento;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspChannelCode;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPspChannelPaymentTypes;
import static it.gov.pagopa.apiconfig.TestUtil.getMockTipoVersamento;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

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

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.exception.AppException;
import it.gov.pagopa.apiconfig.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.gov.pagopa.apiconfig.model.psp.PspChannelCode;
import it.gov.pagopa.apiconfig.model.psp.PspChannelList;
import it.gov.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.service.PspService;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.repository.CanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.CanaliRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspCanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspRepository;
import it.gov.pagopa.apiconfig.starter.repository.TipiVersamentoRepository;

@SpringBootTest(classes = ApiConfig.class)
class PspServiceTest {

  public static final String PSP_CODE = "1234ABC12345";

  @MockBean PspRepository pspRepository;

  @MockBean PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

  @MockBean private CanaliRepository canaliRepository;

  @MockBean private TipiVersamentoRepository tipiVersamentoRepository;

  @MockBean private CanaleTipoVersamentoRepository canaleTipoVersamentoRepository;

  @InjectMocks @Autowired PspService pspService;

  @Test
  void getPaymentServiceProviders() throws IOException, JSONException {
    Page<Psp> page = TestUtil.mockPage(Lists.newArrayList(getMockPsp()), 50, 0);
    when(pspRepository.findAll(
            any(org.springframework.data.domain.Example.class), any(Pageable.class)))
        .thenReturn(page);

    PaymentServiceProviders result =
        pspService.getPaymentServiceProviders(50, 0, getMockFilterAndOrder(Order.Psp.CODE));
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_paymentserviceproviders_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getPaymentServiceProvider() throws IOException, JSONException {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));

    PaymentServiceProviderDetails result = pspService.getPaymentServiceProvider(PSP_CODE);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_paymentserviceprovider_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getPaymentServiceProvider_notFound() {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.empty());

    try {
      pspService.getPaymentServiceProvider(PSP_CODE);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createPsp() throws IOException, JSONException {
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.empty());
    when(pspRepository.save(any(Psp.class))).thenReturn(getMockPsp());

    var result = pspService.createPaymentServiceProvider(getMockPaymentServiceProviderDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/create_psp_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createPsp_conflict() {
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.of(getMockPsp()));

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
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.of(getMockPsp()));
    when(pspRepository.save(any(Psp.class))).thenReturn(getMockPsp());

    var result =
        pspService.updatePaymentServiceProvider(PSP_CODE, getMockPaymentServiceProviderDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/update_psp_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void updatePsp_notFound() {
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.empty());
    try {
      pspService.updatePaymentServiceProvider(PSP_CODE, getMockPaymentServiceProviderDetails());
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deletePsp() {
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.of(getMockPsp()));

    pspService.deletePaymentServiceProvider(PSP_CODE);
    assertTrue(true);
  }

  @Test
  void deletePsp_notfound() {
    when(pspRepository.findByIdPsp(PSP_CODE)).thenReturn(Optional.empty());

    try {
      pspService.deletePaymentServiceProvider(PSP_CODE);
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getPaymentServiceProvidersChannels() throws IOException, JSONException {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(pspCanaleTipoVersamentoRepository.findByFkPsp(anyLong()))
        .thenReturn(Lists.newArrayList(getMockPspCanaleTipoVersamento()));

    PspChannelList result = pspService.getPaymentServiceProvidersChannels(PSP_CODE);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_psp_channels_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createPaymentServiceProvidersChannels() throws IOException, JSONException {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));

    var result =
        pspService.createPaymentServiceProvidersChannels(PSP_CODE, getMockPspChannelCode());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/create_psp_channels_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createPaymentServiceProvidersChannels_400() {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));

    PspChannelCode mock = getMockPspChannelCode();
    mock.setPaymentTypeList(new ArrayList<>());
    try {
      pspService.createPaymentServiceProvidersChannels(PSP_CODE, mock);
      fail();
    } catch (Exception e) {
      assertEquals(AppException.class, e.getClass());
    }
  }

  @Test
  void updatePaymentServiceProvidersChannels() throws IOException, JSONException {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));

    var result =
        pspService.updatePaymentServiceProvidersChannels(
            PSP_CODE, "2", getMockPspChannelPaymentTypes());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/update_psp_channels_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void updatePaymentServiceProvidersChannels_400() {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));

    try {
      PspChannelPaymentTypes paymentTypes = new PspChannelPaymentTypes();
      paymentTypes.setPaymentTypeList(new ArrayList<>());
      pspService.updatePaymentServiceProvidersChannels(PSP_CODE, "2", paymentTypes);
      fail();
    } catch (Exception e) {
      assertEquals(AppException.class, e.getClass());
    }
  }

  @Test
  void deletePaymentServiceProvidersChannels() {
    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(getMockPsp()));
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));

    try {
      pspService.deletePaymentServiceProvidersChannels(PSP_CODE, "2");
    } catch (Exception e) {
      fail();
    }
  }
}
