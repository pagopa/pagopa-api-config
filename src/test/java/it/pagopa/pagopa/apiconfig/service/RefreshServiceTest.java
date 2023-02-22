package it.pagopa.pagopa.apiconfig.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import feign.Feign;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
import it.pagopa.pagopa.apiconfig.util.RefreshClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ApiConfig.class)
class RefreshServiceTest {
  @Autowired private RefreshService refreshService;
  private MockClient mockClient;

  @BeforeEach
  void setUp() {
    mockClient =
        new MockClient()
            .ok(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue(), "SUCCESS")
            .ok(
                HttpMethod.GET,
                "/jobs/trigger/" + JobTrigger.REFRESH_CONFIGURATION.getValue(),
                "SUCCESS")
            .ok(
                HttpMethod.GET,
                "/config/refresh/" + ConfigurationDomain.FTP_SERVER.getValue(),
                "SUCCESS")
            .add(
                HttpMethod.GET,
                "/config/refresh/" + ConfigurationDomain.PA.getValue(),
                500) // simulates a server-side error
            .add(
                HttpMethod.GET,
                "/jobs/trigger/" + JobTrigger.PA_RETRY_PA_INVIA_RT_NEGATIVE.getValue(),
                500);

    RefreshClient refreshClient =
        Feign.builder().client(mockClient).target(new MockTarget<>(RefreshClient.class));
    refreshService.setClient(refreshClient);
  }

  @Test
  void refreshDomainConfig() {
    // valid param case: picking one among ConfigDomain values
    String response = refreshService.refreshConfig(ConfigurationDomain.FTP_SERVER);
    mockClient.verifyOne(
        HttpMethod.GET, "/config/refresh/" + ConfigurationDomain.FTP_SERVER.getValue());
    assertEquals("SUCCESS", response);
  }

  @Test
  void refreshGlobalConfig() {
    String response = refreshService.refreshConfig(ConfigurationDomain.GLOBAL);
    mockClient.verifyOne(
        HttpMethod.GET, "/jobs/trigger/" + JobTrigger.REFRESH_CONFIGURATION.getValue());
    assertEquals("SUCCESS", response);
  }

  @Test
  void jobTrigger() {
    // valid param case: picking one among JobTrigger values
    String response = refreshService.jobTrigger(JobTrigger.PA_INVIA_RT);
    mockClient.verifyOne(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue());
    assertEquals("SUCCESS", response);
  }

  @Test
  void jobTrigger_500() {
    // simulates a server-side error
    Throwable exception =
        assertThrows(
            AppException.class,
            () -> refreshService.jobTrigger(JobTrigger.PA_RETRY_PA_INVIA_RT_NEGATIVE));
    assertEquals("Something was wrong", exception.getMessage());
  }

  @Test
  void refreshDomainConfig_500() {
    // simulates a server-side error
    Throwable exception =
        assertThrows(
            AppException.class, () -> refreshService.refreshConfig(ConfigurationDomain.PA));
    assertEquals("Something was wrong", exception.getMessage());
  }
}
