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
import it.gov.pagopa.apiconfig.core.service.CdsService;
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

    when(cdsService.getCdsServices()).thenReturn(List.of(cdsServizio));
    when(cdsService.getCdsService(anyString())).thenReturn(cdsServizio);
    when(cdsService.createCdsService(any(CdsServizio.class))).thenReturn(cdsServizio);
    when(cdsService.updateCdsService(anyString(), any(CdsServizio.class))).thenReturn(cdsServizio);
  }

  @Test
  void getCdsServices() throws Exception {
    mvc.perform(get("/cds/service").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCdsService() throws Exception {
    mvc.perform(get("/cds/service/service-1").contentType(MediaType.APPLICATION_JSON))
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
            post("/cds/service")
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
            put("/cds/service/service-1")
                .content(TestUtil.toJson(request))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCdsService() throws Exception {
    mvc.perform(delete("/cds/service/service-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
