package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIntermediariePa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockStationDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitutions;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Stations;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
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
class StationsServiceTest {

  @MockBean private StazioniRepository stazioniRepository;

  @MockBean private PaStazionePaRepository paStazionePaRepository;

  @MockBean private IntermediariPaRepository intermediariPaRepository;

  @MockBean private PaRepository paRepository;

  @Autowired @InjectMocks private StationsService stationsService;

  @Test
  void getStations() throws IOException, JSONException {
    Page<Stazioni> page = TestUtil.mockPage(Lists.newArrayList(getMockStazioni()), 50, 0);
    when(stazioniRepository.findAllByFilters(
            anyLong(), anyLong(), anyString(), any(Pageable.class)))
        .thenReturn(page);
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.ofNullable(getMockPa()));
    when(intermediariPaRepository.findByIdIntermediarioPa(anyString()))
        .thenReturn(Optional.ofNullable(getMockIntermediariePa()));

    Stations result =
        stationsService.getStations(
            50, 0, "1234", "4321", getMockFilterAndOrder(Order.CreditorInstitution.CODE));
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_stations_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getStation() throws IOException, JSONException {
    when(stazioniRepository.findByIdStazione("80007580279_01"))
        .thenReturn(Optional.of(getMockStazioni()));

    StationDetails result = stationsService.getStation("80007580279_01");
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_station_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createStation() throws IOException, JSONException {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.empty());
    when(stazioniRepository.save(any(Stazioni.class))).thenReturn(getMockStazioni());
    when(intermediariPaRepository.findByIdIntermediarioPa(anyString()))
        .thenReturn(Optional.of(getMockIntermediariePa()));

    StationDetails result = stationsService.createStation(getMockStationDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/create_station_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void createStation_conflict() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));

    try {
      stationsService.createStation(getMockStationDetails());
    } catch (AppException e) {
      assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void updateStation() throws IOException, JSONException {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    when(stazioniRepository.save(any(Stazioni.class))).thenReturn(getMockStazioni());
    when(intermediariPaRepository.findByIdIntermediarioPa(anyString()))
        .thenReturn(Optional.of(getMockIntermediariePa()));

    StationDetails result = stationsService.updateStation("1234", getMockStationDetails());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/update_station_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void updateStation_notFound() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.empty());
    try {
      stationsService.updateStation("1234", getMockStationDetails());
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deleteStation() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));

    stationsService.deleteStation("1234");
    assertTrue(true);
  }

  @Test
  void deleteStation_notfound() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.empty());

    try {
      stationsService.deleteStation("1234");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getStationCreditorInstitutions() throws IOException, JSONException {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    Page<PaStazionePa> page = TestUtil.mockPage(Lists.newArrayList(getMockPaStazionePa()), 50, 0);
    when(paStazionePaRepository.findAllByFkStazione_ObjId(any(), any(Pageable.class)))
        .thenReturn(page);

    StationCreditorInstitutions result =
        stationsService.getStationCreditorInstitutions("1234", 50, 0);
    String actual = TestUtil.toJson(result);
    String expected =
        TestUtil.readJsonFromFile("response/get_station_creditorinstitutions_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getStationCreditorInstitutionsCsv() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    when(paStazionePaRepository.findAllByFkStazione_ObjId(any()))
        .thenReturn(Lists.newArrayList(getMockPaStazionePa()));

    var result = stationsService.getStationCreditorInstitutionsCSV("1234");
    assertNotNull(result);
  }

  @Test
  void getStationsCsv() {
    when(stazioniRepository.findAll()).thenReturn(Lists.newArrayList(getMockStazioni()));

    String result = new String(stationsService.getStationsCSV());
    assertTrue(result.contains("80007580279_01"));
    assertTrue(result.contains("Regione Lazio"));
  }

  @Test
  void getStationCreditorInstitutionRelation() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.ofNullable(getMockPa()));
    when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(any(), any()))
        .thenReturn(Optional.ofNullable(getMockPaStazionePa()));

    var result = stationsService.getStationCreditorInstitutionRelation("1234", "1234");
    assertNotNull(result);
    assertEquals("00168480242", result.getCreditorInstitutionCode());
  }

  @Test
  void getStationCreditorInstitutionRelation_notFoundEC() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.empty());
    when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(any(), any()))
        .thenReturn(Optional.empty());
    try {
      stationsService.getStationCreditorInstitutionRelation("1234", "1234");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void getStationCreditorInstitutionRelation_notFoundRelation() {
    when(stazioniRepository.findByIdStazione("1234")).thenReturn(Optional.of(getMockStazioni()));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.ofNullable(getMockPa()));
    when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(any(), any()))
        .thenReturn(Optional.empty());
    try {
      stationsService.getStationCreditorInstitutionRelation("1234", "1234");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }
}
