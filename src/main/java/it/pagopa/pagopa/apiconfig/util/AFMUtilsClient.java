package it.pagopa.pagopa.apiconfig.util;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.pagopa.pagopa.apiconfig.model.afm.CdiCosmos;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AFMUtilsClient {
  @RequestLine("POST /cdis/sync")
  @Headers({"Content-Type: application/json", "Ocp-Apim-Subscription-Key: {subscriptionKey}", "X-Request-Id: {requestId}"})
  void syncPaymentTypes(@Param String subscriptionKey, @Param String requestId, List<CdiCosmos> body);
}
