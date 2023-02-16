package it.pagopa.pagopa.apiconfig.service;

import feign.Feign;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
import it.pagopa.pagopa.apiconfig.util.RefreshClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ApiConfig.class)
public class RefreshServiceTest {
    @Autowired
    private RefreshService refreshService;
    private MockClient mockClient;

      @BeforeEach
      void setUp() {
          mockClient = new MockClient()
              .ok(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue(), "SUCCESS")
              .ok(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.GLOBAL.getValue(), "SUCCESS")
              .ok(HttpMethod.GET, "/config/refresh/" + ConfigurationDomain.FTP_SERVER.getValue(), "SUCCESS");

          RefreshClient refreshClient = Feign.builder()
              .client(mockClient)
              .target(new MockTarget<>(RefreshClient.class));

          refreshService.setClient(refreshClient);
      }

    @Test
    void refreshDomainConfig() {
        // valid param case: picking one among ConfigDomain values
        String response = refreshService.refreshConfig(ConfigurationDomain.FTP_SERVER);
        mockClient.verifyOne(HttpMethod.GET, "/config/refresh/" + ConfigurationDomain.FTP_SERVER.getValue());
        assertEquals("SUCCESS", response);
    }

    @Test
    void refreshGlobalConfig() {
        String response = refreshService.jobTrigger(JobTrigger.GLOBAL);
        mockClient.verifyOne(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.GLOBAL.getValue());
        assertEquals("SUCCESS", response);
    }

    @Test
    void jobTrigger() {
        // valid param case: picking one among JobTrigger values
        String response = refreshService.jobTrigger(JobTrigger.PA_INVIA_RT);
        mockClient.verifyOne(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue());
        assertEquals("SUCCESS", response);
    }
}
