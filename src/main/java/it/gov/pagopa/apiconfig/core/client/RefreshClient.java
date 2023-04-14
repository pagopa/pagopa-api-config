package it.gov.pagopa.apiconfig.core.client;

import feign.Param;
import feign.RequestLine;
import org.springframework.stereotype.Service;

@Service
public interface RefreshClient {
  @RequestLine("GET /jobs/trigger/{jobTrigger}")
  String triggerJob(@Param("jobTrigger") String jobTrigger);

  @RequestLine("GET /config/refresh/{configDomain}")
  String refreshConfiguration(@Param("configDomain") String configDomain);
}
