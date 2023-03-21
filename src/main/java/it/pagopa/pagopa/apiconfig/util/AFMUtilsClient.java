package it.pagopa.pagopa.apiconfig.util;

import feign.Headers;
import feign.RequestLine;
import it.pagopa.pagopa.apiconfig.cosmos.container.PaymentTypesCosmos;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AFMUtilsClient {
  @RequestLine("POST /paymenttypes")
  @Headers("Content-Type: application/json")
  void syncPaymentTypes(List<PaymentTypesCosmos> body);
}


