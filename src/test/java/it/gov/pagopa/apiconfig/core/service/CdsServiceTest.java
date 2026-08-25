package it.gov.pagopa.apiconfig.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import it.gov.pagopa.apiconfig.starter.repository.CdsServizioRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ApiConfig.class)
class CdsServiceTest {

  @MockBean private CdsServizioRepository cdsServizioRepository;

  @Autowired @InjectMocks private CdsService cdsService;

  @Test
  void getCdsServices() {
    CdsServizio cdsServizio = getMockCdsServizio();
    when(cdsServizioRepository.findAllFetching()).thenReturn(List.of(cdsServizio));

    List<CdsServizio> result = cdsService.getCdsServices();

    assertEquals(1, result.size());
    assertEquals("service-1", result.get(0).getIdServizio());
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
}
