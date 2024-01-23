package it.gov.pagopa.apiconfig.core.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ApiConfigCacheClient {

  @RequestLine("GET /stakeholders/node/cache/refresh")
  @Headers({
          "Content-Type: application/json",
          "Ocp-Apim-Subscription-Key: {subscriptionKey}"
  })
  ResponseEntity<String> refresh(@Param String subscriptionKey);

}
