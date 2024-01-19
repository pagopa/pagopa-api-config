package it.gov.pagopa.apiconfig.core.service;

import feign.Feign;
import feign.mock.HttpMethod;
import feign.mock.MockClient;
import feign.mock.MockTarget;
import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.client.RefreshClient;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.ConfigurationDomain;
import it.gov.pagopa.apiconfig.core.model.JobTrigger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ApiConfig.class)
class RefreshServiceTest {

  public static final String JSON_SUCCESS_PA_INVIA_RT_JOB_TRIGGER =
      "{\"success\":true,\"action\":\"Trigger job paInviaRt\",\"description\":\"Job triggered\"}";
  public static final String JSON_SUCCESS_REFRESH_CONFIGURATION_JOB_TRIGGER =
      "{\"success\":true,\"action\":\"Trigger job refreshConfiguration\",\"description\":\"Job"
          + " triggered\"}";

  private static final String API_CONFIG_CACHE_REFRESH_API= "/stakeholders/node/cache/refresh";

  @Autowired private RefreshService refreshService;
  private MockClient mockClient;

  @BeforeEach
  void setUp() {
    mockClient =
        new MockClient()
            .ok(
                HttpMethod.GET,
                "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue(),
                JSON_SUCCESS_PA_INVIA_RT_JOB_TRIGGER)
            .ok(
                HttpMethod.GET,
                "/jobs/trigger/" + JobTrigger.REFRESH_CONFIGURATION.getValue(),
                JSON_SUCCESS_REFRESH_CONFIGURATION_JOB_TRIGGER)
            .ok(
                    HttpMethod.GET,
                    API_CONFIG_CACHE_REFRESH_API,
                    "")
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
    assertEquals(
        "{\"success\":true,\"action\":\"Trigger job refreshConfiguration\",\"description\":\"Job"
            + " triggered\"}",
        response);
  }

  @Test
  void refreshApiConfigCache() {
    String response = refreshService.refreshConfig(ConfigurationDomain.GLOBAL);
    mockClient.verifyOne(
            HttpMethod.GET, API_CONFIG_CACHE_REFRESH_API);
    assertEquals(
            "{\"success\":true,\"action\":\"Trigger job refreshConfiguration\",\"description\":\"Job"
                    + " triggered\"}",
            response);
  }

  @Test
  void jobTrigger() {
    // valid param case: picking one among JobTrigger values
    String response = refreshService.jobTrigger(JobTrigger.PA_INVIA_RT);
    mockClient.verifyOne(HttpMethod.GET, "/jobs/trigger/" + JobTrigger.PA_INVIA_RT.getValue());
    assertEquals(
        "{\"success\":true,\"action\":\"Trigger job paInviaRt\",\"description\":\"Job triggered\"}",
        response);
  }

  @Test
  void jobTrigger_500() {
    // simulates a server-side error
    Throwable exception =
        assertThrows(
            AppException.class,
            () -> refreshService.jobTrigger(JobTrigger.PA_RETRY_PA_INVIA_RT_NEGATIVE));
    assertEquals("Refresh configuration failure", exception.getMessage());
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
