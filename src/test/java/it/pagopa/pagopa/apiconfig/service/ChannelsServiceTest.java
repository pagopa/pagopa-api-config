package it.pagopa.pagopa.apiconfig.service;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCanaleTipoVersamento;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCanali;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockChannelDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIntermediariePsp;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPspCanaleTipoVersamento;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPspChannelPaymentTypes;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockTipoVersamento;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockWfespPluginConf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.pagopa.pagopa.apiconfig.repository.CanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = ApiConfig.class)
class ChannelsServiceTest {

  @MockBean private CanaliRepository canaliRepository;

  @MockBean private IntermediariPspRepository intermediariPspRepository;

  @MockBean private WfespPluginConfRepository wfespPluginConfRepository;

  @MockBean CanaleTipoVersamentoRepository canaleTipoVersamentoRepository;

  @MockBean TipiVersamentoRepository tipiVersamentoRepository;

  @MockBean PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

  @Autowired @InjectMocks private ChannelsService channelsService;

  @Test
  void getChannels() throws IOException, JSONException {
    Page<Canali> page = TestUtil.mockPage(Lists.newArrayList(getMockCanali()), 50, 0);
    when(canaliRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    Channels result = channelsService.getChannels(50, 0, getMockFilterAndOrder(Order.Channel.CODE));
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_channels_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getChannel() throws IOException, JSONException {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));

    ChannelDetails result = channelsService.getChannel("1234");
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_channel_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getChannel_notFound() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());

    try {
      channelsService.getChannel("1234");
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createChannel() throws IOException, JSONException {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());
    when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
    when(intermediariPspRepository.findByIdIntermediarioPsp(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
    when(wfespPluginConfRepository.findByIdServPlugin(anyString()))
        .thenReturn(Optional.ofNullable(getMockWfespPluginConf()));

    ChannelDetails result = channelsService.createChannel(getMockChannelDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/create_channel_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createChannel_404() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());
    when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
    when(intermediariPspRepository.findByIdIntermediarioPsp(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
    AppException exception =
        new AppException(AppError.SERV_PLUGIN_NOT_FOUND, "SERV_PLUGIN_NOT_FOUND");
    doThrow(exception).when(wfespPluginConfRepository).findByIdServPlugin(anyString());

    ChannelDetails channelDetails = getMockChannelDetails();
    channelDetails.setServPlugin("UNKNOWN");
    try {
      channelsService.createChannel(channelDetails);
      fail("no exception thrown");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createChannel_conflict() {
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(getMockCanali()));
    when(intermediariPspRepository.findByIdIntermediarioPsp(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
    when(wfespPluginConfRepository.findByIdServPlugin(anyString()))
        .thenReturn(Optional.ofNullable(getMockWfespPluginConf()));

    ChannelDetails mockChannelDetails = getMockChannelDetails();
    try {
      channelsService.createChannel(mockChannelDetails);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void updateChannel() throws IOException, JSONException {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
    when(intermediariPspRepository.findByIdIntermediarioPsp(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
    when(wfespPluginConfRepository.findByIdServPlugin(anyString()))
        .thenReturn(Optional.ofNullable(getMockWfespPluginConf()));

    ChannelDetails result = channelsService.updateChannel("1234", getMockChannelDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/update_channel_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void updateChannel_noPlugin() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(canaliRepository.save(any(Canali.class))).thenReturn(getMockCanali());
    when(intermediariPspRepository.findByIdIntermediarioPsp(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePsp()));
    when(wfespPluginConfRepository.findByIdServPlugin(anyString())).thenReturn(Optional.empty());

    ChannelDetails mockChannelDetails = getMockChannelDetails();
    try {
      channelsService.updateChannel("1234", mockChannelDetails);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void updateChannel_notFound() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());
    try {
      channelsService.updateChannel("1234", getMockChannelDetails());
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deleteChannel() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));

    channelsService.deleteChannel("1234");
    assertTrue(true);
  }

  @Test
  void deleteChannel_notfound() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.empty());

    try {
      channelsService.deleteChannel("1234");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deleteChannel_badRequest() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    DataIntegrityViolationException exception = Mockito.mock(DataIntegrityViolationException.class);
    doThrow(exception).when(canaliRepository).delete(any(Canali.class));

    try {
      channelsService.deleteChannel("1234");
      fail("no exception thrown");
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getPaymentTypes() throws IOException, JSONException {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(canaleTipoVersamentoRepository.findByFkCanale(anyLong()))
        .thenReturn(Lists.newArrayList(getMockCanaleTipoVersamento()));

    var result = channelsService.getPaymentTypes("1234");
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_paymenttype_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createPaymentType() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    try {
      channelsService.createPaymentType("1234", getMockPspChannelPaymentTypes());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createPaymentType_409() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));
    PspChannelPaymentTypes paymentTypes = getMockPspChannelPaymentTypes();
    paymentTypes.setPaymentTypeList(List.of("PO"));
    try {
      channelsService.createPaymentType("1234", paymentTypes);
      fail("no exception thrown");
    } catch (AppException e) {
      assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void createPaymentType_400() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    try {
      PspChannelPaymentTypes paymentTypes = getMockPspChannelPaymentTypes();
      paymentTypes.setPaymentTypeList(new ArrayList<>());
      channelsService.createPaymentType("1234", paymentTypes);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deletePaymentType() {
    when(canaliRepository.findByIdCanale("1234")).thenReturn(Optional.of(getMockCanali()));
    when(tipiVersamentoRepository.findByTipoVersamento(anyString()))
        .thenReturn(Optional.of(getMockTipoVersamento()));
    when(canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockCanaleTipoVersamento()));
    try {
      channelsService.deletePaymentType("1234", "PO");
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getChannelsPaymentServiceProviders() throws JSONException, IOException {
    when(pspCanaleTipoVersamentoRepository.findByCanaleTipoVersamento_Canale_IdCanale(anyString()))
        .thenReturn(Lists.newArrayList(getMockPspCanaleTipoVersamento()));

    var result = channelsService.getChannelPaymentServiceProviders(10, 0, "1234", null, null, null);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_channelsPSP_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getChannelsPaymentServiceProvidersWithFilters() throws JSONException, IOException {
    when(pspCanaleTipoVersamentoRepository.findByCanaleTipoVersamento_Canale_IdCanale(anyString()))
        .thenReturn(Lists.newArrayList(getMockPspCanaleTipoVersamento()));

    var result =
        channelsService.getChannelPaymentServiceProviders(
            10, 0, "1234", null, "Regione Lazio", Boolean.TRUE);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_channelsPSP_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getStationCreditorInstitutionsCsv() {
    when(pspCanaleTipoVersamentoRepository.findByCanaleTipoVersamento_Canale_IdCanale(anyString()))
        .thenReturn(Lists.newArrayList(getMockPspCanaleTipoVersamento()));

    var result = channelsService.getChannelPaymentServiceProvidersCSV("1234");
    assertNotNull(result);
  }

  @Test
  void getChannelsCsv() {
    when(canaliRepository.findAll()).thenReturn(Lists.newArrayList(getMockCanali()));

    String result = new String(channelsService.getChannelsCSV());
    assertTrue(result.contains("Regione Lazio"));
    assertTrue(result.contains("1234"));
  }
}
