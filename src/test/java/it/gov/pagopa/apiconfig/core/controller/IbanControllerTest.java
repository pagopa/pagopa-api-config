package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEnhanced;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEnhanced_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbans;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbansEnhanced;
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
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
import java.time.OffsetDateTime;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class IbanControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CreditorInstitutionsService creditorInstitutionsService;

  @MockBean private IbanService ibanService;

  @BeforeEach
  void setUp() {
    when(creditorInstitutionsService.getCreditorInstitutionsIbans("1234"))
        .thenReturn(getMockIbans());
    when(ibanService.getCreditorInstitutionsIbansByLabel(anyString(), anyString()))
        .thenReturn(
            getMockIbansEnhanced(
                OffsetDateTime.parse("2023-06-07T13:48:15.166+02"),
                OffsetDateTime.parse("2023-06-07T13:48:15.166+02")));
  }

  @ParameterizedTest
  @CsvSource({"/creditorinstitutions/1234/ibans"})
  void testGets(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @ParameterizedTest
  @CsvSource({"/creditorinstitutions/1234/ibans/enhanced?label=STANDIN"})
  void testGetsEnhanced(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createIban_201() throws Exception {
    mvc.perform(
            post("/creditorinstitutions/1234/ibans")
                .content(
                    TestUtil.toJson(
                        getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now())))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  void createIban_400() throws Exception {
    IbanEnhanced ibanEnhanced = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    when(ibanService.createIban(anyString(), any(IbanEnhanced.class)))
        .thenThrow(ConstraintViolationException.class);
    mvc.perform(
            post("/creditorinstitutions/1234/ibans")
                .content(TestUtil.toJson(ibanEnhanced))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createIban_422() throws Exception {
    IbanEnhanced ibanEnhanced = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    ibanEnhanced.getLabels().get(0).setName("FAKE");
    when(ibanService.createIban(anyString(), any(IbanEnhanced.class)))
        .thenThrow(new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "", ""));
    mvc.perform(
            post("/creditorinstitutions/1234/ibans")
                .content(TestUtil.toJson(ibanEnhanced))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createIban_500() throws Exception {
    when(ibanService.createIban(anyString(), any(IbanEnhanced.class)))
        .thenThrow(OptimisticLockingFailureException.class);
    mvc.perform(
            post("/creditorinstitutions/1234/ibans")
                .content(
                    TestUtil.toJson(
                        getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now())))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateIban_200() throws Exception {
    mvc.perform(
            put("/creditorinstitutions/1234/ibans/IT99C0222211111000000000000")
                .content(
                    TestUtil.toJson(
                        getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now())))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void updateIban_400() throws Exception {
    IbanEnhanced ibanEnhanced = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    when(ibanService.updateIban(anyString(), anyString(), any(IbanEnhanced.class)))
        .thenThrow(ConstraintViolationException.class);
    mvc.perform(
            put("/creditorinstitutions/1234/ibans/fake")
                .content(TestUtil.toJson(ibanEnhanced))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateIban_422() throws Exception {
    IbanEnhanced ibanEnhanced = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    ibanEnhanced.getLabels().get(0).setName("IT99C0222211111000000000000");
    when(ibanService.updateIban(anyString(), anyString(), any(IbanEnhanced.class)))
        .thenThrow(new AppException(HttpStatus.UNPROCESSABLE_ENTITY, "", ""));
    mvc.perform(
            put("/creditorinstitutions/1234/ibans/IT99C0222211111000000000000")
                .content(TestUtil.toJson(ibanEnhanced))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void updateIban_500() throws Exception {
    when(ibanService.updateIban(anyString(), anyString(), any(IbanEnhanced.class)))
        .thenThrow(OptimisticLockingFailureException.class);
    mvc.perform(
            put("/creditorinstitutions/1234/ibans/IT99C0222211111000000000000")
                .content(
                    TestUtil.toJson(
                        getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now())))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteIban() throws Exception {
    mvc.perform(
        post("/creditorinstitutions/1234/ibans")
            .content(TestUtil.toJson(getMockIbanEnhanced_2()))
            .contentType(MediaType.APPLICATION_JSON));
    mvc.perform(
            delete("/creditorinstitutions/1234/ibans/IT99C0222211111000000000003")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
