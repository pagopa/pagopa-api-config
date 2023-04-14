package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import it.gov.pagopa.apiconfig.core.exception.AppException;

public enum Protocol {
  HTTPS("HTTPS"),
  HTTP("HTTP");

  private final String value;

  Protocol(String value) {
    this.value = value;
  }

  public static Protocol fromValue(String value) {
    return Arrays.stream(Protocol.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst()
        .orElseThrow(
            () ->
                new AppException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Protocol not found",
                    "Cannot convert string '" + value + "' into enum"));
  }
}
