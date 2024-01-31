package it.gov.pagopa.apiconfig.core.controller;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.model.JobTrigger;
import it.gov.pagopa.apiconfig.core.service.RefreshService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class RefreshControllerTest {
  @Autowired private MockMvc mvc;

  @MockBean private RefreshService refreshService;

  @BeforeEach
  void setUp() {
    when(refreshService.refreshConfig()).thenReturn("SUCCESS");
    when(refreshService.jobTrigger(any(JobTrigger.class))).thenReturn("SUCCESS");
  }

  @Test
  void refreshGlobalService() throws Exception {
    String url = "/refresh/config/";
    mvc.perform(get(url)).andExpect(status().isOk());
  }

  @Test
  void refreshService_400() throws Exception {
    String url = "/refresh/config/NOT_A_VALID_DOMAIN";
    mvc.perform(get(url)).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }

  @Test
  void jobTrigger() throws Exception {
    // valid param case: picking one among JobTrigger values
    String url = "/refresh/job/" + JobTrigger.PA_INVIA_RT;
    mvc.perform(get(url)).andExpect(status().isOk());
  }

  @Test
  void jobTrigger_400() throws Exception {
    String url = "/refresh/job/NOT_A_VALID_JOB";
    mvc.perform(get(url)).andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
  }
}
