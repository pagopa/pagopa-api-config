package it.gov.pagopa.apiconfig.core.client;

import feign.FeignException;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import it.gov.pagopa.apiconfig.core.model.afm.CdiCosmos;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public interface AFMUtilsClient {
  @RequestLine("POST /cdis/sync")
  @Headers({
    "Content-Type: application/json",
    "Ocp-Apim-Subscription-Key: {subscriptionKey}",
    "X-Request-Id: {requestId}"
  })
  void syncPaymentTypes(
      @Param String subscriptionKey, @Param String requestId, List<CdiCosmos> body);

  @Retryable(
      exclude = FeignException.FeignClientException.class,
      maxAttemptsExpression = "${retry.utils.maxAttempts:1}",
      backoff = @Backoff(delayExpression = "${retry.utils.maxDelay:1000}"))
  @RequestLine("DELETE /psps/{pspCode}/cdis/{idCdi}")
  @Headers({
    "Content-Type: " + MediaType.APPLICATION_JSON_VALUE,
    "Ocp-Apim-Subscription-Key: {subscriptionKey}",
    "X-Request-Id: {requestId}"
  })
  ResponseEntity<Void> deleteBundlesByIdCDI(
      @Param String subscriptionKey,
      @Param String requestId,
      @Param("idCdi") String idCdi,
      @Param("pspCode") String pspCode);
}
