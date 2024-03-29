package it.gov.pagopa.apiconfig.core.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.gov.pagopa.apiconfig.core.model.afm.PaymentTypesCosmos;
import it.gov.pagopa.apiconfig.core.model.configuration.AfmMarketplacePaymentType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AFMMarketplaceClient {
  @RequestLine("POST /paymenttypes")
  @Headers({
    "Content-Type: application/json",
    "Ocp-Apim-Subscription-Key: {subscriptionKey}",
    "X-Request-Id: {requestId}"
  })
  void syncPaymentTypes(
      @Param String subscriptionKey, @Param String requestId, List<PaymentTypesCosmos> body);

  @RequestLine("GET /paymenttypes/{paymentTypeCode}")
  @Headers({
    "Content-Type: application/json",
    "Ocp-Apim-Subscription-Key: {subscriptionKey}",
    "X-Request-Id: {requestId}"
  })
  AfmMarketplacePaymentType getPaymentType(
      @Param String subscriptionKey, @Param String requestId, @Param String paymentTypeCode);
}
