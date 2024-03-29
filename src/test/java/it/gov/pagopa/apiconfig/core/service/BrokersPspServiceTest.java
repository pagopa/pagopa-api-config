package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockBrokerPspDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIntermediariePsp;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPsp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.core.model.psp.BrokerPspDetails;
import it.gov.pagopa.apiconfig.core.model.psp.BrokersPsp;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPspRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspRepository;
import java.io.IOException;
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

@SpringBootTest(classes = ApiConfig.class)
class BrokersPspServiceTest {

  @MockBean private IntermediariPspRepository intermediariPspRepository;
  @MockBean private PspRepository pspRepository;

  @Autowired @InjectMocks private BrokersPspService brokersPspService;

  @Test
  void getBrokersPsp() throws IOException, JSONException {
    Page<IntermediariPsp> page =
        TestUtil.mockPage(Lists.newArrayList(getMockIntermediariePsp()), 50, 0);
    when(intermediariPspRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    BrokersPsp result =
        brokersPspService.getBrokersPsp(50, 0, getMockFilterAndOrder(Order.BrokerPsp.CODE));
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_brokerspsp_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getBrokerPsp() throws IOException, JSONException {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234"))
        .thenReturn(Optional.of(getMockIntermediariePsp()));

    BrokerPspDetails result = brokersPspService.getBrokerPsp("1234");
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_brokerpsp_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getBrokerPsp_notFound() {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.empty());

    try {
      brokersPspService.getBrokerPsp("123");
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createBrokerPsp() throws IOException, JSONException {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.empty());
    when(intermediariPspRepository.save(any(IntermediariPsp.class)))
        .thenReturn(getMockIntermediariePsp());

    BrokerPspDetails result = brokersPspService.createBrokerPsp(getMockBrokerPspDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/create_brokerpsp_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createBrokerPsp_conflict() {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234"))
        .thenReturn(Optional.of(getMockIntermediariePsp()));
    BrokerPspDetails mockBrokerPspDetails = getMockBrokerPspDetails();
    try {
      brokersPspService.createBrokerPsp(mockBrokerPspDetails);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void updateBrokerPsp() throws IOException, JSONException {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234"))
        .thenReturn(Optional.of(getMockIntermediariePsp()));

    BrokerPspDetails result = brokersPspService.updateBrokerPsp("1234", getMockBrokerPspDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/update_brokerpsp_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void updateBrokerPsp_notFound() {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.empty());
    try {
      brokersPspService.updateBrokerPsp("1234", getMockBrokerPspDetails());
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deleteBrokerPsp() {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234"))
        .thenReturn(Optional.of(getMockIntermediariePsp()));

    brokersPspService.deleteBrokerPsp("1234");
    assertTrue(true);
  }

  @Test
  void deleteBrokerPsp_notfound() {
    when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.empty());

    try {
      brokersPspService.deleteBrokerPsp("1234");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getPspBrokerPsp() throws IOException, JSONException {
    Page<Psp> page = TestUtil.mockPage(Lists.newArrayList(getMockPsp()), 50, 0);
    when(pspRepository
            .findAllByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_fkIntermediarioPsp_idIntermediarioPsp(
                anyString(), any(Pageable.class)))
        .thenReturn(page);

    var result = brokersPspService.getPspBrokerPsp(10, 0, "1234");
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_psp_brokerpsp.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
}
