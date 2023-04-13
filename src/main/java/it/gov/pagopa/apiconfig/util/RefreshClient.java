package it.gov.pagopa.apiconfig.util;

import org.springframework.stereotype.Service;

import feign.Param;
import feign.RequestLine;

@Service
public interface RefreshClient {
  @RequestLine("GET /jobs/trigger/{jobTrigger}")
  String triggerJob(@Param("jobTrigger") String jobTrigger);

  @RequestLine("GET /config/refresh/{configDomain}")
  String refreshConfiguration(@Param("configDomain") String configDomain);
}
