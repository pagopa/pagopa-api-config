package it.gov.pagopa.apiconfig.core.service;

import feign.Feign;
import feign.FeignException;
import feign.Response;
import it.gov.pagopa.apiconfig.core.client.ApiConfigCacheClient;
import it.gov.pagopa.apiconfig.core.client.RefreshClient;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.JobTrigger;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.CompletableFuture;

@Service
@Validated
@Setter
@Slf4j
@Transactional
public class RefreshService {

  public static final String SUCCESS = "SUCCESS";
  private RefreshClient client;
  private ApiConfigCacheClient apiConfigCacheClient;

  @Value("${service.api-config-cache.refresh}") private boolean apiConfigCacheRefresh;
  @Value("${service.api-config-cache.subscriptionKey}") private String apiConfigCacheSubscriptionKey;

  @Value("${service.nodo-monitoring.refresh}") private boolean monitoringRefresh;

  public RefreshService(@Value("${service.nodo-monitoring.host}") String monitoringUrl, @Value("${service.api-config-cache.host}") String apiConfigCacheUrl) {
    client = Feign.builder().target(RefreshClient.class, monitoringUrl);
    apiConfigCacheClient = Feign.builder().target(ApiConfigCacheClient.class, apiConfigCacheUrl);
  }

  public String jobTrigger(JobTrigger jobType) {
    return callJobTrigger(jobType);
  }

  public String refreshConfig() {
    String response = "OK";
    if( apiConfigCacheRefresh ) {
      callApiConfigCache();
    }
    return response;
  }

  private void callApiConfigCache() {
    CompletableFuture.supplyAsync(
      () -> {
        try {
          log.debug("RefreshService api-config-cache refresh");
          Response response = apiConfigCacheClient.refresh(apiConfigCacheSubscriptionKey);
          int httpResponseCode = response.status();
          if (httpResponseCode != HttpStatus.OK.value()) {
            log.error("RefreshService api-config-cache refresh error - result: httpStatusCode[{}]", httpResponseCode);
          } else {
            log.info("RefreshService api-config-cache refresh successful");
          }
        } catch (FeignException.GatewayTimeout e) {
          log.error("RefreshService api-config-cache refresh error: Gateway timeout", e);
        } catch (FeignException e) {
          log.error("RefreshService api-config-cache refresh error", e);
        }
        return null;
    });
  }

  private String callJobTrigger(JobTrigger jobType) {
    String response;
    try {
      response = client.triggerJob(jobType.getValue());
    } catch (FeignException.GatewayTimeout e) {
      throw new AppException(AppError.REFRESH_CONFIG_TIMEOUT, e);
    } catch (FeignException e) {
      throw new AppException(AppError.REFRESH_CONFIG_EXCEPTION, e);
    }
    log.debug("RefreshService job trigger: {}", response);
    return response;
  }

}
