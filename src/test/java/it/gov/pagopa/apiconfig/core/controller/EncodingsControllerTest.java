package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionEncodings;
import static it.gov.pagopa.apiconfig.TestUtil.getMockEncoding;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.service.EncodingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class EncodingsControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private EncodingsService encdodingService;

  @BeforeEach
  void setUp() {
    when(encdodingService.getCreditorInstitutionEncodings("1234"))
        .thenReturn(getMockCreditorInstitutionEncodings());
    when(encdodingService.createCreditorInstitutionEncoding(anyString(), any(Encoding.class)))
        .thenReturn(getMockEncoding());
  }

  @Test
  void getEncodings() throws Exception {
    mvc.perform(get("/creditorinstitutions/1234/encodings").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createEncoding() throws Exception {
    mvc.perform(
            post("/creditorinstitutions/1234/encodings")
                .content(TestUtil.toJson(getMockEncoding()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createEncoding_400() throws Exception {
    mvc.perform(
            post("/creditorinstitutions/1234/encodings")
                .content(TestUtil.toJson(getMockEncoding().toBuilder().encodingCode("").build()))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteEncoding() throws Exception {
    mvc.perform(
            delete("/creditorinstitutions/1234/encodings/111")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
