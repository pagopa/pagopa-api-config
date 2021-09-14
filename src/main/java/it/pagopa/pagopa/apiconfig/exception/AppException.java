package it.pagopa.pagopa.apiconfig.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;

/**
 * Custom exception.
 * <p> See {@link it.pagopa.pagopa.apiconfig.exception.ErrorHandler}
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class AppException extends RuntimeException {

    /**
     * title returned to the response when this exception occurred
     */
    String title;

    /**
     * http status returned to the response when this exception occurred
     */
    HttpStatus httpStatus;

    /**
     * @param httpStatus HTTP status returned to the response
     * @param title title returned to the response when this exception occurred
     * @param message the detail message returend to the response
     * @param cause The cause of this {@link AppException}
     */
    AppException(HttpStatus httpStatus, String title, String message, Throwable cause) {
        super(message, cause);
        this.title = title;
        this.httpStatus = httpStatus;
    }

}
