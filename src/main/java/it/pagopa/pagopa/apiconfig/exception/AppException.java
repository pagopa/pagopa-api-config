package it.pagopa.pagopa.apiconfig.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Value
public class AppException extends RuntimeException {

    /**
     * message to return in the response when this exception occurred
     */
    String details;

    /**
     * http status to return in the response when this exception occurred
     */
    HttpStatus httpStatus;

}
