package it.pagopa.pagopa.apiconfig.service;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBinaryFile;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativePaDetail;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativePaMaster;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.starter.repository.BinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaDetailRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaFasceRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;

@SpringBootTest(classes = ApiConfig.class)
class CounterpartServiceTest {

  @MockBean private InformativePaMasterRepository informativePaMasterRepository;

  @MockBean PaRepository paRepository;

  @MockBean BinaryFileRepository binaryFileRepository;

  @MockBean InformativePaDetailRepository informativePaDetailRepository;

  @MockBean InformativePaFasceRepository informativePaFasceRepository;

  @Autowired @InjectMocks private CounterpartService counterpartService;

  @Test
  void getCounterpartTables() throws IOException, JSONException {
    Page<InformativePaMaster> page =
        TestUtil.mockPage(Lists.newArrayList(getMockInformativePaMaster()), 50, 0);
    when(informativePaMasterRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    CounterpartTables result = counterpartService.getCounterpartTables(50, 0, null, null);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_counterparttables_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getCounterpartTable() {
    when(informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(
            anyString(), anyString()))
        .thenReturn(Optional.of(getMockInformativePaMaster()));

    byte[] result = counterpartService.getCounterpartTable("111", "222");
    assertNotNull(result);
    assertEquals(2, result.length);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "file/counterpart_valid.xml",
        "file/counterpart_valid_2.xml",
        "file/counterpart_valid_3.xml",
        "file/counterpart_valid_4.xml",
        "file/counterpart_valid_5.xml",
      })
  void createCounterpartTable(String arg) throws IOException {
    File xml = TestUtil.readFile(arg);
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    when(informativePaMasterRepository.save(any())).thenReturn(getMockInformativePaMaster());
    when(informativePaDetailRepository.save(any())).thenReturn(getMockInformativePaDetail());
    try {
      counterpartService.createCounterpartTable(file);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void createCounterpartTable_notValid_1() throws IOException {
    File xml = TestUtil.readFile("file/counterpart_not_valid_1.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    when(informativePaMasterRepository.save(any())).thenReturn(getMockInformativePaMaster());
    when(informativePaDetailRepository.save(any())).thenReturn(getMockInformativePaDetail());

    try {
      counterpartService.createCounterpartTable(file);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void deleteCounterpartTable() {
    when(informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(
            anyString(), anyString()))
        .thenReturn(Optional.of(getMockInformativePaMaster()));
    try {
      counterpartService.deleteCounterpartTable("1234", "2");
    } catch (Exception e) {
      fail(e);
    }
  }
}
