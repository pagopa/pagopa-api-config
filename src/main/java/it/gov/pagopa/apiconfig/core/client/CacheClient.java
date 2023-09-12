package it.gov.pagopa.apiconfig.core.client;

import feign.RequestLine;
import org.springframework.stereotype.Service;

@Service
public interface CacheClient {

  @RequestLine("GET /stakeholders/node/cache/schemas/v1?refresh=true")
  String refreshConfiguration();
}
