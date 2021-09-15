package it.pagopa.pagopa.apiconfig.exception;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ApiConfig.class)
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleAppException() {
        AppException appException = new AppException(HttpStatus.BAD_REQUEST, "some error", "details", new NullPointerException());
        ResponseEntity<ProblemJson> actual = errorHandler.handleAppException(appException, null);
        assertEquals(appException.getHttpStatus(), actual.getStatusCode());
        assertEquals(appException.getTitle(), actual.getBody().getTitle());
        assertEquals(appException.getMessage(), actual.getBody().getDetail());
        assertEquals(appException.getHttpStatus().value(), actual.getBody().getStatus());
    }

    @Test
    void handleGenericException() {
        ResponseEntity<ProblemJson> actual = errorHandler.handleGenericException(new NullPointerException("message"), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
        assertEquals("INTERNAL SERVER ERROR", actual.getBody().getTitle());
        assertEquals("message", actual.getBody().getDetail());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getBody().getStatus());
    }
}