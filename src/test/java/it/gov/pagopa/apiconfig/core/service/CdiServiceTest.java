package it.gov.pagopa.apiconfig.core.service;

import feign.FeignException;
import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.psp.Cdis;
import it.gov.pagopa.apiconfig.core.util.AFMUtilsAsyncTask;
import it.gov.pagopa.apiconfig.starter.entity.*;
import it.gov.pagopa.apiconfig.starter.repository.*;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ApiConfig.class)
class CdiServiceTest {

  @MockBean private CdiMasterRepository cdiMasterRepository;

  @MockBean PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

  @MockBean PspRepository pspRepository;
  @MockBean CdiMasterValidRepository cdiMasterValidRepository;

  @MockBean CanaliRepository canaliRepository;

  @MockBean BinaryFileRepository binaryFileRepository;
  @MockBean CdiPreferenceRepository cdiPreferenceRepository;
  @MockBean private CdiDetailRepository cdiDetailRepository;
  @MockBean private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;
  @MockBean private CdiFasciaCostoServizioRepository cdiFasciaCostoServizioRepository;

  @Autowired @InjectMocks private CdiService cdiService;
  @MockBean private AFMUtilsAsyncTask afmUtilsAsyncTask;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(cdiService, "afmUtilsAsyncTask", afmUtilsAsyncTask);
  }

  @Test
  void getCdis() throws IOException, JSONException {
    Page<CdiMaster> page = TestUtil.mockPage(Lists.newArrayList(getMockCdiMaster()), 50, 0);
    when(cdiMasterRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    Cdis result = cdiService.getCdis(50, 0, null, null);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_cdis_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getCdi() {
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp("1234", "1"))
        .thenReturn(Optional.of(getMockCdiMaster()));

    byte[] result = cdiService.getCdi("1234", "1");
    assertNotNull(result);
    assertEquals(2, result.length);
  }

  @Test
  void createCdi() throws IOException {
    File xml = TestUtil.readFile("file/cdi_valid.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("01234567890");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("01234567890");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("01234567890");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(psp));
    CdiMaster mockCdiMaster = getMockCdiMaster();
    mockCdiMaster.setCdiDetail(List.of(getMockCdiDetail()));
    when(cdiMasterRepository.save(any())).thenReturn(mockCdiMaster);
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(channel));
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(List.of(paymentMethod));

    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    when(cdiMasterRepository.save(any())).thenReturn(mockCdiMaster);
    when(pspCanaleTipoVersamentoRepository
            .findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(
                anyLong(), anyString(), anyString()))
        .thenReturn(Optional.of(getMockPspCanaleTipoVersamento()));

    when(afmUtilsAsyncTask.executeSync(any())).thenReturn(true);
    when(cdiDetailRepository.save(any(CdiDetail.class))).thenReturn(getMockCdiDetail());

    cdiService.createCdi(file);

    ArgumentCaptor<CdiDetail> cdiDetail = ArgumentCaptor.forClass(CdiDetail.class);
    verify(cdiDetailRepository, times(1)).save(cdiDetail.capture());
    assertEquals("Pagamento con Carte", cdiDetail.getValue().getNomeServizio());
    assertEquals(1L, cdiDetail.getValue().getPriorita());
    assertEquals(1L, cdiDetail.getValue().getModelloPagamento());
    assertEquals(0L, cdiDetail.getValue().getCanaleApp());
    assertEquals("Visa;Mastercard", cdiDetail.getValue().getTags());
    assertEquals(
        Arrays.toString("YQ==".getBytes()),
        Arrays.toString(cdiDetail.getValue().getLogoServizio()));
    verify(cdiInformazioniServizioRepository, times(5)).save(any());
    verify(cdiFasciaCostoServizioRepository, times(8)).save(any());

    ArgumentCaptor<CdiPreference> cdiPreference = ArgumentCaptor.forClass(CdiPreference.class);
    verify(cdiPreferenceRepository, times(1)).save(cdiPreference.capture());
    assertEquals("MYBANK11", cdiPreference.getValue().getSeller());
    assertEquals(1.00, cdiPreference.getValue().getCostoConvenzione());
    verify(afmUtilsAsyncTask, times(1)).executeSync(mockCdiMaster);
  }

  @Test
  void createCdi_ko_1() throws IOException {
    File xml = TestUtil.readFile("file/cdi_not_valid_3.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    assertThrows(AppException.class, () -> cdiService.createCdi(file));
  }

  @Test
  void createCdi_CHARITY() throws IOException {
    File xml = TestUtil.readFile("file/cdi_valid_charity.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("CHARITYNEXI");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("CHARITYNEXI");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("CHARITYNEXI");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(psp));
    CdiMaster mockCdiMaster = getMockCdiMaster();
    mockCdiMaster.setCdiDetail(List.of(getMockCdiDetail()));
    when(cdiMasterRepository.save(any())).thenReturn(mockCdiMaster);
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(channel));
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(List.of(paymentMethod));

    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    when(cdiMasterRepository.save(any())).thenReturn(mockCdiMaster);
    when(pspCanaleTipoVersamentoRepository
            .findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(
                anyLong(), anyString(), anyString()))
        .thenReturn(Optional.of(getMockPspCanaleTipoVersamento()));

    when(afmUtilsAsyncTask.executeSync(any())).thenReturn(true);

    try {
      cdiService.createCdi(file);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }

    verify(afmUtilsAsyncTask, times(0)).executeSync(mockCdiMaster);
  }

  @Test
  void deleteCdi() {
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(anyString(), anyString()))
        .thenReturn(Optional.of(getMockCdiMaster()));
    try {
      cdiService.deleteCdi("1234", "2");
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void deleteCdi_KO() {
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(anyString(), anyString()))
        .thenReturn(Optional.of(getMockCdiMaster()));
    doThrow(FeignException.InternalServerError.class)
        .when(afmUtilsAsyncTask)
        .afmUtilsDeleteBundlesByIdCDI(anyString(), anyString());
    try {
      cdiService.deleteCdi("1234", "2");
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void checkCdi() throws IOException {
    File xml = TestUtil.readFile("file/cdi_valid.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("01234567890");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("01234567890");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("01234567890");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(psp));
    when(cdiMasterRepository.save(any())).thenReturn(getMockCdiMaster());
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(channel));
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(List.of(paymentMethod));

    List<CheckItem> checkItemList = cdiService.verifyCdi(file);

    assertFalse(
        checkItemList.stream()
            .anyMatch(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)));
  }

  @Test
  void checkCdi_ko_1() throws IOException {
    File xml = TestUtil.readFile("file/cdi_not_valid_1.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("01234567890");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("01234567890");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("01234567890");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.empty());
    when(cdiMasterRepository.save(any())).thenReturn(getMockCdiMaster());
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.empty());
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(anyString(), any()))
        .thenReturn(Optional.empty());
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(List.of(paymentMethod));

    List<CheckItem> checkItemList = cdiService.verifyCdi(file);

    assertEquals(
        2,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.VALID))
            .count());
  }

  @Test
  void checkCdi_ko_2() throws IOException {
    File xml = TestUtil.readFile("file/cdi_not_valid_2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("01234567890");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("01234567890");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("01234567890");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(psp));
    when(cdiMasterRepository.save(any())).thenReturn(getMockCdiMaster());
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(channel));
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(anyString(), anyString()))
        .thenReturn(Optional.empty());
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(new ArrayList<>());

    List<CheckItem> checkItemList = cdiService.verifyCdi(file);

    assertEquals(
        4,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.VALID))
            .count());
  }

  @Test
  void checkCdi_ko_3() throws IOException {
    File xml = TestUtil.readFile("file/cdi_not_valid_2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    Psp psp = getMockPsp();
    psp.setIdPsp("01234567890");
    psp.setAbi("03069");
    psp.setBic("BCITITMM");

    Canali channel = getMockCanali();
    channel.setIdCanale("01234567890");
    channel.getFkIntermediarioPsp().setIdIntermediarioPsp("01234567890");

    PspCanaleTipoVersamento paymentMethod = getMockPspCanaleTipoVersamento();
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setDescrizione("BP");
    paymentMethod.getCanaleTipoVersamento().getTipoVersamento().setTipoVersamento("BP");

    when(pspRepository.findByIdPsp(anyString())).thenReturn(Optional.of(psp));
    when(cdiMasterRepository.save(any())).thenReturn(getMockCdiMaster());
    when(canaliRepository.findByIdCanale(anyString())).thenReturn(Optional.of(channel));
    when(cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(anyString(), anyString()))
        .thenReturn(Optional.of(TestUtil.getMockCdiMaster()));
    when(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            anyLong(), anyLong()))
        .thenReturn(new ArrayList<>());

    List<CheckItem> checkItemList = cdiService.verifyCdi(file);

    assertEquals(
        3,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.VALID))
            .count());
  }

  @Test
  void checkCdi_ko_4() throws IOException {
    File xml = TestUtil.readFile("file/cdi_not_valid_3.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    List<CheckItem> checkItemList = cdiService.verifyCdi(file);
    assertEquals(
        0,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.VALID))
            .count());
    assertEquals(
        1,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .count());
  }

  @Test
  void uploadHistory() {
    CdiMasterValid mockCdiMasterValid = getMockCdiMasterValid();
    mockCdiMasterValid.setCdiDetail(List.of(getMockCdiDetail()));
    when(afmUtilsAsyncTask.executeSync()).thenReturn(true);

    assertDoesNotThrow(() -> cdiService.uploadHistory());
  }
}
