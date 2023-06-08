package it.gov.pagopa.apiconfig.core.controller;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbans;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
public class IbanControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private CreditorInstitutionsService creditorInstitutionsService;

  @MockBean
  private IbanService ibanService;

  @BeforeEach
  void setUp() {
    when(creditorInstitutionsService.getCreditorInstitutionsIbans("1234"))
        .thenReturn(getMockIbans());
  }

  @ParameterizedTest
  @CsvSource({
      "/creditorinstitutions/1234/ibans"
  })
  void testGets(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
