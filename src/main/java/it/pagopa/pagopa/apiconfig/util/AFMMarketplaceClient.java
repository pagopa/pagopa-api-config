package it.pagopa.pagopa.apiconfig.util;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.pagopa.pagopa.apiconfig.model.afm.PaymentTypesCosmos;
import it.pagopa.pagopa.apiconfig.model.configuration.AfmMarketplacePaymentType;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AFMMarketplaceClient {
  @RequestLine("POST /paymenttypes")
  @Headers({"Content-Type: application/json", "Ocp-Apim-Subscription-Key: {subscriptionKey}"})
  void syncPaymentTypes(@Param String subscriptionKey, List<PaymentTypesCosmos> body);

  @RequestLine("GET /paymenttypes/{paymentTypeCode}")
  @Headers({"Content-Type: application/json", "Ocp-Apim-Subscription-Key: {subscriptionKey}"})
  AfmMarketplacePaymentType getPaymentType(
      @Param String subscriptionKey, @Param String paymentTypeCode);
}
