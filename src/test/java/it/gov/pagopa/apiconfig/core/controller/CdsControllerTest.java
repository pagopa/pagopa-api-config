package it.gov.pagopa.apiconfig.core.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.model.cds.CdsServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioRequestDto;
import it.gov.pagopa.apiconfig.core.service.CdsService;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggetto;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class CdsControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CdsService cdsService;

  @BeforeEach
  void setUp() {
    CdsServizio cdsServizio =
        CdsServizio.builder()
            .id(1L)
            .idServizio("service-1")
            .descrizioneServizio("Descrizione servizio")
            .xsdRiferimento("schema.xsd")
            .version(1L)
            .categoriaId(10L)
            .categoria(it.gov.pagopa.apiconfig.starter.entity.CdsCategoria.builder().id(10L).build())
            .build();

    when(cdsService.getCdsServices())
        .thenReturn(CdsServizioList.builder().services(List.of(cdsServizio)).build());
    when(cdsService.getCdsService(anyString())).thenReturn(cdsServizio);
    when(cdsService.createCdsService(any(CdsServizio.class))).thenReturn(cdsServizio);
    when(cdsService.updateCdsService(anyString(), any(CdsServizio.class))).thenReturn(cdsServizio);

    CdsSoggetto cdsSoggetto =
        CdsSoggetto.builder()
            .id(1L)
            .creditorInstitutionCode("CI-1")
            .creditorInstitutionDescription("Descrizione soggetto")
            .build();

    when(cdsService.getCdsSubjects())
        .thenReturn(CdsSoggettoList.builder().subjects(List.of(cdsSoggetto)).build());
    when(cdsService.getCdsSubject(1L)).thenReturn(cdsSoggetto);
    when(cdsService.createCdsSubject(any(CdsSoggetto.class))).thenReturn(cdsSoggetto);
    when(cdsService.updateCdsSubject(any(Long.class), any(CdsSoggetto.class)))
        .thenReturn(cdsSoggetto);

    CdsSoggettoServizio cdsSoggettoServizio =
        CdsSoggettoServizio.builder()
            .id(1L)
            .fkCdsSoggetto("1")
            .fkCdsServizio("service-1")
            .idSoggettoServizio("subject-service-1")
            .descrizioneServizio("Descrizione soggetto servizio")
            .commissione(Boolean.TRUE)
            .build();
    when(cdsService.getCdsSubjectServices(anyString()))
        .thenReturn(
            CdsSoggettoServizioList.builder().subjectServices(List.of(cdsSoggettoServizio)).build());
    when(cdsService.getCdsSubjectService(anyString(), anyString())).thenReturn(cdsSoggettoServizio);
    when(cdsService.createCdsSubjectService(anyString(), any(CdsSoggettoServizioRequestDto.class)))
        .thenReturn(cdsSoggettoServizio);
    when(
            cdsService.updateCdsSubjectService(
                anyString(), anyString(), any(CdsSoggettoServizioRequestDto.class)))
        .thenReturn(cdsSoggettoServizio);
  }

  @Test
  void getCdsServices() throws Exception {
    mvc.perform(get("/cds/services").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCdsService() throws Exception {
    mvc.perform(get("/cds/services/service-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createCdsService() throws Exception {
    CdsServizio request =
        CdsServizio.builder()
            .idServizio("service-1")
            .descrizioneServizio("Descrizione servizio")
            .xsdRiferimento("schema.xsd")
            .version(1L)
            .categoriaId(10L)
            .categoria(it.gov.pagopa.apiconfig.starter.entity.CdsCategoria.builder().id(10L).build())
            .build();

    mvc.perform(
            post("/cds/services")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateCdsService() throws Exception {
    CdsServizio request =
        CdsServizio.builder()
            .idServizio("service-1")
            .descrizioneServizio("Descrizione servizio aggiornata")
            .xsdRiferimento("schema-v2.xsd")
            .version(2L)
            .categoriaId(20L)
            .categoria(it.gov.pagopa.apiconfig.starter.entity.CdsCategoria.builder().id(20L).build())
            .build();

    mvc.perform(
            put("/cds/services/service-1")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCdsService() throws Exception {
    mvc.perform(delete("/cds/services/service-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getCdsSubjects() throws Exception {
    mvc.perform(get("/cds/subjects").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCdsSubject() throws Exception {
    mvc.perform(get("/cds/subjects/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createCdsSubject() throws Exception {
    CdsSoggetto request =
        CdsSoggetto.builder()
            .id(1L)
            .creditorInstitutionCode("CI-1")
            .creditorInstitutionDescription("Descrizione soggetto")
            .build();

    mvc.perform(
            post("/cds/subjects")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateCdsSubject() throws Exception {
    CdsSoggetto request =
        CdsSoggetto.builder()
            .creditorInstitutionCode("CI-1-UPDATED")
            .creditorInstitutionDescription("Descrizione soggetto aggiornata")
            .build();

    mvc.perform(
            put("/cds/subjects/1")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCdsSubject() throws Exception {
    mvc.perform(delete("/cds/subjects/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getCdsSubjectServices() throws Exception {
    mvc.perform(get("/cds/subjects/CI-1/services").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCdsSubjectService() throws Exception {
    mvc.perform(get("/cds/subjects/CI-1/services/subject-service-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createCdsSubjectService() throws Exception {
    CdsSoggettoServizioRequestDto request =
        CdsSoggettoServizioRequestDto.builder()
            .id("subject-service-1")
            .idSoggetto("1")
            .idServizio("service-1")
            .descrizioneServizio("Descrizione soggetto servizio")
            .idStazione("STATION-1")
            .commissione(Boolean.TRUE)
            .build();

    mvc.perform(
            post("/cds/subjects/CI-1/services")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateCdsSubjectService() throws Exception {
    CdsSoggettoServizioRequestDto request =
        CdsSoggettoServizioRequestDto.builder()
            .id("subject-service-1")
            .idSoggetto("1")
            .idServizio("service-1")
            .descrizioneServizio("Descrizione soggetto servizio aggiornata")
            .idStazione("STATION-1")
            .commissione(Boolean.FALSE)
            .build();

    mvc.perform(
            put("/cds/subjects/CI-1/services/subject-service-1")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCdsSubjectService() throws Exception {
    mvc.perform(delete("/cds/subjects/CI-1/services/subject-service-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
