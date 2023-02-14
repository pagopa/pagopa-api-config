package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.service.RefreshService.ConfigDomain;
import it.pagopa.pagopa.apiconfig.service.RefreshService.JobTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
public class RefreshServiceTest {

    @Autowired
    @InjectMocks
    private RefreshService refreshService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
      ResponseEntity<String> responseEntityOK = new ResponseEntity<>("SUCCESS", HttpStatus.OK);
      String mock_url = "mock_url/";
      String validConfigDomain = mock_url+ConfigDomain.FTP_SERVER;
      String validJobTrigger = mock_url+JobTrigger.paInviaRt;
      String globalConfig = mock_url+"refreshConfiguration";

      String regexValidArg = "(^" +
          validConfigDomain + "$|^" +
          validJobTrigger + "$|^" +
          globalConfig + "$)";

      when(restTemplate.exchange(
            matches(regexValidArg),
            eq(HttpMethod.GET),
            ArgumentMatchers.<HttpEntity<Void>>any(),
            ArgumentMatchers.<Class<String>>any()
      )).thenReturn(responseEntityOK);
    }

    @Test
    void refreshDomainConfig() {
        // valid param case: picking one among ConfigDomain values
        String response = refreshService.refreshConfig(ConfigDomain.FTP_SERVER);
        assertEquals("SUCCESS", response);
    }

    @Test
    void refreshGlobalConfig() {
      String response = refreshService.refreshConfig(ConfigDomain.Global);
      assertEquals("SUCCESS", response);
    }

    @Test
    void jobTrigger() {
      // valid param case: picking one among JobTrigger values
      String response = refreshService.jobTrigger(JobTrigger.paInviaRt);
      assertEquals("SUCCESS", response);
    }
}
