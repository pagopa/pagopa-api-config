package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.service.RefreshService;
import it.pagopa.pagopa.apiconfig.service.RefreshService.ConfigDomain;
import it.pagopa.pagopa.apiconfig.service.RefreshService.JobTrigger;
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
public class RefreshControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RefreshService refreshService;

    @BeforeEach
    void setUp() {
        when(refreshService.refreshConfig(any(ConfigDomain.class))).thenReturn("SUCCESS");
        when(refreshService.jobTrigger(any(JobTrigger.class))).thenReturn("SUCCESS");
    }

    @Test
    void refreshService() throws Exception {
      // valid param case: picking one among ConfigDomain values
      String url = "/refresh/config/" + ConfigDomain.FTP_SERVER;
      mvc.perform(get(url))
          .andExpect(status().isOk());
    }

    @Test
    void refreshGlobalService() throws Exception {
      String url = "/refresh/config/";
      mvc.perform(get(url))
          .andExpect(status().isOk());
    }

    @Test
    void refreshService_400() throws Exception {
      String url = "/refresh/config/NOT_A_VALID_DOMAIN";
      mvc.perform(get(url))
          .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void jobTrigger() throws Exception {
      // valid param case: picking one among JobTrigger values
      String url = "/refresh/job/" + JobTrigger.paInviaRt;
      mvc.perform(get(url))
          .andExpect(status().isOk());
    }

    @Test
    void jobTrigger_400() throws Exception {
      String url = "/refresh/job/NOT_A_VALID_JOB";
      mvc.perform(get(url))
          .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}
