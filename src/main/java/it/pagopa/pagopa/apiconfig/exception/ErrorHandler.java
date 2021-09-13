package it.pagopa.pagopa.apiconfig.exception;

import it.pagopa.pagopa.apiconfig.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {


    /**
     * Handle if a {@link AppException} is raised
     *
     * @param ex      {@link AppException} exception raised
     * @param request from frontend
     * @return a {@link ErrorResponse} as response with the cause and with an appropriated HTTP status
     */
    @ExceptionHandler({AppException.class})
    public ResponseEntity<ErrorResponse> handleAppException(final AppException ex, final WebRequest request) {
        var errorResponse = ErrorResponse.builder()
                .message(ex.getDetails())
                .build();
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }



    /**
     * Handle if a {@link Exception} is raised
     *
     * @param ex      {@link Exception} exception raised
     * @param request from frontend
     * @return a {@link ErrorResponse} as response with the cause and with 500 as HTTP status
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> handleGenericException(final Exception ex, final WebRequest request) {
        var errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
