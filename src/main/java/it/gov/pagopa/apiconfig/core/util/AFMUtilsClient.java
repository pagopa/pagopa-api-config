package it.gov.pagopa.apiconfig.core.util;

import java.util.List;

import org.springframework.stereotype.Service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.gov.pagopa.apiconfig.core.model.afm.CdiCosmos;

@Service
public interface AFMUtilsClient {
  @RequestLine("POST /cdis/sync")
  @Headers({"Content-Type: application/json", "Ocp-Apim-Subscription-Key: {subscriptionKey}"})
  void syncPaymentTypes(@Param String subscriptionKey, List<CdiCosmos> body);
}