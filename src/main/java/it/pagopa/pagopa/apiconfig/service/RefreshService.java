package it.pagopa.pagopa.apiconfig.service;

import feign.Feign;
import feign.FeignException;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
import it.pagopa.pagopa.apiconfig.util.RefreshClient;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Setter
public class RefreshService {

  private RefreshClient client;

  public RefreshService(@Value("${service.nodo-monitoring.host}") String monitoringUrl) {
    client = Feign.builder().target(RefreshClient.class, monitoringUrl);
  }

  public String jobTrigger(JobTrigger jobType) {
    try {
      return client.triggerJob(jobType.getValue());
    } catch (FeignException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }
  }

  public String refreshConfig(ConfigurationDomain domain) {
    try {
      if (domain.equals(ConfigurationDomain.GLOBAL))
        return client.triggerJob(JobTrigger.REFRESH_CONFIGURATION.getValue());
      else return client.refreshConfiguration(domain.getValue());
    } catch (FeignException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }
  }
}
