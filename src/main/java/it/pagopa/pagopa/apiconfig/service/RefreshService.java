package it.pagopa.pagopa.apiconfig.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.FeignException;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
import it.pagopa.pagopa.apiconfig.util.RefreshClient;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Setter
@Slf4j
public class RefreshService {

  public static final String SUCCESS = "SUCCESS";
  private RefreshClient client;

  public RefreshService(@Value("${service.nodo-monitoring.host}") String monitoringUrl) {
    client = Feign.builder().target(RefreshClient.class, monitoringUrl);
  }

  public String jobTrigger(JobTrigger jobType) {
    return callJobTrigger(jobType);
  }

  public String refreshConfig(ConfigurationDomain domain) {
    String response;
    if (domain.equals(ConfigurationDomain.GLOBAL)) {
      response = callJobTrigger(JobTrigger.REFRESH_CONFIGURATION);
    } else {
      response = callRefreshConfigDomain(domain);
    }

    return response;
  }

  private String callJobTrigger(JobTrigger jobType) {
    String response;

    try {
      response = client.triggerJob(jobType.getValue());
    } catch (FeignException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
    log.debug("RefreshService job trigger: {}", response);
    try {
      JsonNode responseJson = new ObjectMapper().readValue(response, JsonNode.class);
      if (!responseJson.get("success").asBoolean()) {
        throw new AppException(AppError.REFRESH_CONFIG_EXCEPTION);
      }
    } catch (JsonProcessingException e) {
      throw new AppException(AppError.REFRESH_CONFIG_EXCEPTION, e);
    }

    return response;
  }

  private String callRefreshConfigDomain(ConfigurationDomain domain) {
    String response;

    try {
      response = client.refreshConfiguration(domain.getValue());
    } catch (FeignException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
    log.debug("RefreshService refresh domain configuration: {}", response);
    if (!response.equalsIgnoreCase(SUCCESS)) {
      throw new AppException(AppError.REFRESH_CONFIG_EXCEPTION);
    }

    return response;
  }
}
