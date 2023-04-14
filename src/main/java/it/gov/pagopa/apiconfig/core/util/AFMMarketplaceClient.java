package it.gov.pagopa.apiconfig.core.util;

import java.util.List;

import org.springframework.stereotype.Service;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.gov.pagopa.apiconfig.core.model.afm.PaymentTypesCosmos;
import it.gov.pagopa.apiconfig.core.model.configuration.AfmMarketplacePaymentType;

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