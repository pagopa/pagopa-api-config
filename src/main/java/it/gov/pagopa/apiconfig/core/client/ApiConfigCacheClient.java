package it.gov.pagopa.apiconfig.core.client;

import feign.RequestLine;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ApiConfigCacheClient {
  @RequestLine("GET /stakeholders/node/cache/refresh")
  ResponseEntity<String> refresh();

}
