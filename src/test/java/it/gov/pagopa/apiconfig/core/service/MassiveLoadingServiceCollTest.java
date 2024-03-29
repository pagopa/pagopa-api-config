package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest(classes = ApiConfig.class)
class MassiveLoadingServiceCollTest {

  @MockBean private PaRepository paRepository;

  @MockBean private StazioniRepository stazioniRepository;

  @MockBean private PaStazionePaRepository paStazionePaRepository;

  @Autowired @InjectMocks private MassiveLoadingService massiveLoadingService;

  @Test
  void manageCIStationRelationship() throws IOException {
    File csv = TestUtil.readFile("file/ci_station_ok.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione(anyString()))
        .thenReturn(Optional.of(getMockStazioni()));
    when(paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    try {
      massiveLoadingService.manageCIStation(multipartFile);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void manageCIStationRelationship_ko_1() throws IOException {
    File csv = TestUtil.readFile("file/ci_station_ok.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.empty());
    when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.empty());
    when(paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    try {
      massiveLoadingService.manageCIStation(multipartFile);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void manageCIStationRelationship_ko_2() throws IOException {
    File csv = TestUtil.readFile("file/ci_station_ok.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione(anyString()))
        .thenReturn(Optional.of(getMockStazioni()));
    when(paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
        .thenReturn(Optional.of(getMockPaStazionePa()));

    try {
      massiveLoadingService.manageCIStation(multipartFile);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void manageCIStationRelationship_ko_3() throws IOException {
    File csv = TestUtil.readFile("file/ci_station_ok.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.empty());
    when(paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
        .thenReturn(Optional.empty());

    try {
      massiveLoadingService.manageCIStation(multipartFile);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void manageCIStationRelationship_ko_4() throws IOException {
    File csv = TestUtil.readFile("file/ci_station_ko_1.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione(anyString()))
        .thenReturn(Optional.of(getMockStazioni()));
    when(paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
        .thenReturn(Optional.empty());
    try {
      massiveLoadingService.manageCIStation(multipartFile);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    } catch (Exception e) {
      fail(e);
    }
  }
}
