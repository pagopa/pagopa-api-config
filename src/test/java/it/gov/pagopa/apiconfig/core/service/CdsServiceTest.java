package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.cds.CdsServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioRequestDto;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggetto;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.CdsSoggettoRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsSoggettoServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ApiConfig.class)
class CdsServiceTest {

  @MockBean private CdsServizioRepository cdsServizioRepository;
  @MockBean private CdsSoggettoRepository cdsSoggettoRepository;
  @MockBean private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @MockBean private PaStazionePaRepository paStazionePaRepository;
  @MockBean private PaRepository paRepository;
  @MockBean private StazioniRepository stazioniRepository;
  @MockBean private EntityManager entityManager;

  @Autowired @InjectMocks private CdsService cdsService;

  @Test
  void getCdsServices() {
    CdsServizio cdsServizio = getMockCdsServizio();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(cdsServizio));

    CdsServizioList result = cdsService.getCdsServices();

    assertEquals(1, result.getServices().size());
    assertEquals("service-1", result.getServices().get(0).getIdServizio());
  }

  @Test
  void getCdsService() {
    CdsServizio cdsServizio = getMockCdsServizio();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(cdsServizio));

    CdsServizio result = cdsService.getCdsService("service-1");

    assertEquals("service-1", result.getIdServizio());
  }

  @Test
  void createCdsService() {
    CdsServizio request =
        CdsServizio.builder()
            .idServizio("service-1")
            .descrizioneServizio("Descrizione servizio")
            .xsdRiferimento("schema.xsd")
            .version(1L)
            .categoriaId(10L)
            .build();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of());
    when(cdsServizioRepository.save(any(CdsServizio.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CdsServizio result = cdsService.createCdsService(request);

    assertEquals("service-1", result.getIdServizio());
    assertEquals(1L, result.getVersion());
    assertEquals(10L, result.getCategoriaId());
    assertEquals(10L, result.getCategoria().getId());
    verify(cdsServizioRepository, times(1)).save(any(CdsServizio.class));
  }

  @Test
  void createCdsService_conflict() {
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));

    AppException exception =
        assertThrows(AppException.class, () -> cdsService.createCdsService(getMockCdsServizio()));

    assertEquals(AppError.CDS_SERVIZIO_CONFLICT.getHttpStatus(), exception.getHttpStatus());
  }

  @Test
  void updateCdsService() {
    CdsServizio existing = getMockCdsServizio();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(existing));
    when(cdsServizioRepository.save(any(CdsServizio.class))).thenAnswer(invocation -> invocation.getArgument(0));

    CdsServizio result =
        cdsService.updateCdsService(
            "service-1",
            CdsServizio.builder()
                .idServizio("service-1")
                .descrizioneServizio("Descrizione aggiornata")
                .xsdRiferimento("schema-v2.xsd")
                .version(2L)
                .categoriaId(20L)
                .build());

    assertEquals("Descrizione aggiornata", result.getDescrizioneServizio());
    assertEquals(2L, result.getVersion());
    assertEquals(20L, result.getCategoriaId());
    assertEquals(20L, result.getCategoria().getId());
    verify(cdsServizioRepository, times(1)).save(existing);
  }

  @Test
  void deleteCdsService() {
    CdsServizio existing = getMockCdsServizio();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(existing));

    cdsService.deleteCdsService("service-1");

    verify(cdsServizioRepository, times(1)).delete(existing);
  }

  @Test
  void getCdsSubjects() {
    CdsSoggetto cdsSoggetto = getMockCdsSoggetto();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(cdsSoggetto));

    CdsSoggettoList result = cdsService.getCdsSubjects();

    assertEquals(1, result.getSubjects().size());
    assertEquals(1L, result.getSubjects().get(0).getId());
  }

  @Test
  void getCdsSubject() {
    CdsSoggetto cdsSoggetto = getMockCdsSoggetto();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(cdsSoggetto));

    CdsSoggetto result = cdsService.getCdsSubject(1L);

    assertEquals(1L, result.getId());
  }

  @Test
  void createCdsSubject() {
    CdsSoggetto request =
        CdsSoggetto.builder()
            .id(1L)
            .creditorInstitutionCode("CI-1")
            .creditorInstitutionDescription("Descrizione soggetto")
            .build();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of());
    when(paRepository.findByIdDominio("CI-1")).thenReturn(Optional.of(getMockPa()));
    when(cdsSoggettoRepository.save(any(CdsSoggetto.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CdsSoggetto result = cdsService.createCdsSubject(request);

    assertEquals(1L, result.getId());
    assertEquals("CI-1", result.getCreditorInstitutionCode());
    assertEquals("Descrizione soggetto", result.getCreditorInstitutionDescription());
    verify(cdsSoggettoRepository, times(1)).save(any(CdsSoggetto.class));
  }

  @Test
  void createCdsSubject_conflict() {
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));

    AppException exception =
        assertThrows(AppException.class, () -> cdsService.createCdsSubject(getMockCdsSoggetto()));

    assertEquals(AppError.CDS_SOGGETTO_CONFLICT.getHttpStatus(), exception.getHttpStatus());
  }

  @Test
  void updateCdsSubject() {
    CdsSoggetto existing = getMockCdsSoggetto();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(existing));
    when(paRepository.findByIdDominio("CI-1-UPDATED")).thenReturn(Optional.of(getMockPa()));
    when(cdsSoggettoRepository.save(any(CdsSoggetto.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CdsSoggetto result =
        cdsService.updateCdsSubject(
            1L,
            CdsSoggetto.builder()
                .creditorInstitutionCode("CI-1-UPDATED")
                .creditorInstitutionDescription("Descrizione aggiornata")
                .build());

    assertEquals("CI-1-UPDATED", result.getCreditorInstitutionCode());
    assertEquals("Descrizione aggiornata", result.getCreditorInstitutionDescription());
    verify(cdsSoggettoRepository, times(1)).save(existing);
  }

  @Test
  void createCdsSubject_creditorInstitutionNotFound() {
    CdsSoggetto request =
        CdsSoggetto.builder()
            .id(1L)
            .creditorInstitutionCode("CI-NOT-FOUND")
            .creditorInstitutionDescription("Descrizione soggetto")
            .build();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of());
    when(paRepository.findByIdDominio("CI-NOT-FOUND")).thenReturn(Optional.empty());

    AppException exception = assertThrows(AppException.class, () -> cdsService.createCdsSubject(request));

    assertEquals(AppError.CREDITOR_INSTITUTION_NOT_FOUND.getHttpStatus(), exception.getHttpStatus());
  }

  @Test
  void updateCdsSubject_creditorInstitutionNotFound() {
    CdsSoggetto existing = getMockCdsSoggetto();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(existing));
    when(paRepository.findByIdDominio("CI-NOT-FOUND")).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(
            AppException.class,
            () ->
                cdsService.updateCdsSubject(
                    1L,
                    CdsSoggetto.builder()
                        .creditorInstitutionCode("CI-NOT-FOUND")
                        .creditorInstitutionDescription("Descrizione aggiornata")
                        .build()));

    assertEquals(AppError.CREDITOR_INSTITUTION_NOT_FOUND.getHttpStatus(), exception.getHttpStatus());
  }

  @Test
  void deleteCdsSubject() {
    CdsSoggetto existing = getMockCdsSoggetto();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(existing));

    cdsService.deleteCdsSubject(1L);

    verify(cdsSoggettoRepository, times(1)).delete(existing);
  }

  @Test
  void getCdsSubjectServices() {
    CdsSoggettoServizio cdsSoggettoServizio = getMockCdsSoggettoServizio();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));
    when(paStazionePaRepository.findAllFetching()).thenReturn(List.of(getMockPaStazionePa()));
    when(cdsSoggettoServizioRepository.findAllFetching()).thenReturn(List.of(cdsSoggettoServizio));

    CdsSoggettoServizioList result = cdsService.getCdsSubjectServices("CI-1");

    assertEquals(1, result.getSubjectServices().size());
    assertEquals("subject-service-1", result.getSubjectServices().get(0).getIdSoggettoServizio());
    assertEquals(
        "STATION-1",
        result.getSubjectServices().get(0).getStazionePa().getFkStazione().getIdStazione());
    assertEquals("service-1", result.getSubjectServices().get(0).getServizio().getIdServizio());
  }

  @Test
  void getCdsSubjectService() {
    CdsSoggettoServizio cdsSoggettoServizio = getMockCdsSoggettoServizio();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));
    when(paStazionePaRepository.findAllFetching()).thenReturn(List.of(getMockPaStazionePa()));
    when(cdsSoggettoServizioRepository.findAllFetching()).thenReturn(List.of(cdsSoggettoServizio));

    CdsSoggettoServizio result = cdsService.getCdsSubjectService("CI-1", "subject-service-1");

    assertEquals("subject-service-1", result.getIdSoggettoServizio());
    assertEquals("service-1", result.getServizio().getIdServizio());
  }

  @Test
  void createCdsSubjectService() {
    ZonedDateTime requestStart = ZonedDateTime.parse("2026-08-31T00:00:00Z");
    ZonedDateTime persistedStart = ZonedDateTime.parse("2026-08-31T02:00:00+02:00");
    CdsSoggettoServizioRequestDto request =
        CdsSoggettoServizioRequestDto.builder()
            .id("subject-service-1")
            .idSoggetto("1")
            .idServizio("service-1")
            .descrizioneServizio("Descrizione soggetto servizio")
            .dataInizioValidita(requestStart)
            .idStazione("STATION-1")
            .commissione(Boolean.TRUE)
            .build();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));
    when(paRepository.findByIdDominio("CI-1")).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione("STATION-1"))
        .thenReturn(Optional.of(Stazioni.builder().objId(1L).idStazione("STATION-1").build()));
    when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(1L, 1L))
        .thenReturn(Optional.of(getMockPaStazionePa()));
    when(paStazionePaRepository.findAllFetching()).thenReturn(List.of(getMockPaStazionePa()));
    when(cdsSoggettoServizioRepository.findAllFetching()).thenReturn(List.of());
    when(cdsSoggettoServizioRepository.saveAndFlush(any(CdsSoggettoServizio.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    doAnswer(
            invocation -> {
              CdsSoggettoServizio entity = invocation.getArgument(0);
              entity.setDataInizioValidita(persistedStart);
              return null;
            })
        .when(entityManager)
        .refresh(any(CdsSoggettoServizio.class));

    CdsSoggettoServizio result = cdsService.createCdsSubjectService("CI-1", request);

    assertEquals("1", result.getFkCdsSoggetto());
    assertEquals("1", result.getFkCdsServizio());
    assertEquals("subject-service-1", result.getIdSoggettoServizio());
    assertEquals("service-1", result.getServizio().getIdServizio());
    assertEquals(persistedStart, result.getDataInizioValidita());
    verify(cdsSoggettoServizioRepository, times(1)).saveAndFlush(any(CdsSoggettoServizio.class));
  }

  @Test
  void createCdsSubjectService_conflict() {
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));
    when(cdsSoggettoServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsSoggettoServizio()));

    AppException exception =
        assertThrows(
            AppException.class,
            () -> cdsService.createCdsSubjectService("CI-1", getMockCdsSoggettoServizioRequestDto()));

    assertEquals(AppError.CDS_SOGGETTO_SERVIZIO_CONFLICT.getHttpStatus(), exception.getHttpStatus());
  }

  @Test
  void updateCdsSubjectService() {
    ZonedDateTime requestStart = ZonedDateTime.parse("2026-08-31T00:00:00Z");
    ZonedDateTime persistedStart = ZonedDateTime.parse("2026-08-31T02:00:00+02:00");
    CdsSoggettoServizio existing = getMockCdsSoggettoServizio();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(getMockCdsServizio()));
    when(paRepository.findByIdDominio("CI-1")).thenReturn(Optional.of(getMockPa()));
    when(stazioniRepository.findByIdStazione("STATION-1"))
        .thenReturn(Optional.of(Stazioni.builder().objId(1L).idStazione("STATION-1").build()));
    when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(1L, 1L))
        .thenReturn(Optional.of(getMockPaStazionePa()));
    when(paStazionePaRepository.findAllFetching()).thenReturn(List.of(getMockPaStazionePa()));
    when(cdsSoggettoServizioRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(cdsSoggettoServizioRepository.saveAndFlush(any(CdsSoggettoServizio.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    doAnswer(
            invocation -> {
              CdsSoggettoServizio entity = invocation.getArgument(0);
              entity.setDataInizioValidita(persistedStart);
              return null;
            })
        .when(entityManager)
        .refresh(any(CdsSoggettoServizio.class));

    CdsSoggettoServizio result =
        cdsService.updateCdsSubjectService(
            "CI-1",
            "subject-service-1",
            CdsSoggettoServizioRequestDto.builder()
                .id("1")
                .idSoggetto("1")
                .idServizio("service-1")
                .descrizioneServizio("Descrizione aggiornata")
                .dataInizioValidita(requestStart)
                .idStazione("STATION-1")
                .commissione(Boolean.FALSE)
                .build());

    assertEquals("Descrizione aggiornata", result.getDescrizioneServizio());
    assertEquals(Boolean.FALSE, result.getCommissione());
    assertEquals("1", result.getFkCdsServizio());
    assertEquals("service-1", result.getServizio().getIdServizio());
    assertEquals(persistedStart, result.getDataInizioValidita());
    verify(cdsSoggettoServizioRepository, times(1)).saveAndFlush(existing);
  }

  @Test
  void deleteCdsSubjectService() {
    CdsSoggettoServizio existing = getMockCdsSoggettoServizio();
    when(cdsSoggettoRepository.findAll()).thenReturn(List.of(getMockCdsSoggetto()));
    when(cdsSoggettoServizioRepository.findAllFetching()).thenReturn(List.of(existing));

    cdsService.deleteCdsSubjectService("CI-1", "subject-service-1");

    verify(cdsSoggettoServizioRepository, times(1)).delete(existing);
  }

  private CdsServizio getMockCdsServizio() {
    return CdsServizio.builder()
        .id(1L)
        .idServizio("service-1")
        .descrizioneServizio("Descrizione servizio")
        .xsdRiferimento("schema.xsd")
        .version(1L)
        .categoriaId(10L)
        .build();
  }

  private CdsSoggetto getMockCdsSoggetto() {
    return CdsSoggetto.builder()
        .id(1L)
        .creditorInstitutionCode("CI-1")
        .creditorInstitutionDescription("Descrizione soggetto")
        .build();
  }

  private CdsSoggettoServizio getMockCdsSoggettoServizio() {
    return CdsSoggettoServizio.builder()
        .id(1L)
        .fkCdsSoggetto("1")
        .fkCdsServizio("1")
        .fkStazione("1")
        .idSoggettoServizio("subject-service-1")
        .descrizioneServizio("Descrizione soggetto servizio")
        .dataInizioValidita(ZonedDateTime.parse("2026-08-31T02:00:00+02:00"))
        .commissione(Boolean.TRUE)
        .build();
  }

  private CdsSoggettoServizioRequestDto getMockCdsSoggettoServizioRequestDto() {
    return CdsSoggettoServizioRequestDto.builder()
        .id("subject-service-1")
        .idSoggetto("1")
        .idServizio("service-1")
        .descrizioneServizio("Descrizione soggetto servizio")
        .idStazione("STATION-1")
        .commissione(Boolean.TRUE)
        .build();
  }

  private PaStazionePa getMockPaStazionePa() {
    return PaStazionePa.builder()
        .objId(1L)
        .fkStazione(Stazioni.builder().idStazione("STATION-1").build())
        .build();
  }
}
