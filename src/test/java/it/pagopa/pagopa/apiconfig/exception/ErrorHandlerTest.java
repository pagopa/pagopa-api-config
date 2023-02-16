package it.pagopa.pagopa.apiconfig.exception;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class ErrorHandlerTest {

    @Autowired
    private ErrorHandler errorHandler;

    @Test
    void handleAppException() {
        AppException appException = new AppException(HttpStatus.BAD_REQUEST, "some error", "details", new NullPointerException());
        ResponseEntity<ProblemJson> actual = errorHandler.handleAppException(appException, null);
        assertEquals(appException.getHttpStatus(), actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(appException.getTitle(), actual.getBody().getTitle());
        assertEquals(appException.getMessage(), actual.getBody().getDetail());
        assertEquals(appException.getHttpStatus().value(), actual.getBody().getStatus());
    }

    @Test
    void handleAppException2() {
        AppException appException = new AppException(HttpStatus.BAD_REQUEST, "some error", "details", null);
        ResponseEntity<ProblemJson> actual = errorHandler.handleAppException(appException, null);
        assertEquals(appException.getHttpStatus(), actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(appException.getTitle(), actual.getBody().getTitle());
        assertEquals(appException.getMessage(), actual.getBody().getDetail());
        assertEquals(appException.getHttpStatus().value(), actual.getBody().getStatus());
    }

    @Test
    void handleGenericException() {
        ResponseEntity<ProblemJson> actual = errorHandler.handleGenericException(new NullPointerException("message"), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals("INTERNAL SERVER ERROR", actual.getBody().getTitle());
        assertEquals("message", actual.getBody().getDetail());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getBody().getStatus());
    }

    @Test
    void handleMissingServletRequestParameter() {
        ResponseEntity<Object> actual = errorHandler.handleMissingServletRequestParameter(new MissingServletRequestParameterException("param", "String"), null, null, null);
        assertNotNull(actual.getBody());
        ProblemJson body = (ProblemJson) actual.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals("BAD REQUEST", body.getTitle());
        assertEquals("Required request parameter 'param' for method parameter type String is not present", body.getDetail());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }

    @Test
    void handleDataIntegrityViolationException() {
        ResponseEntity<ProblemJson> actual = errorHandler.handleDataIntegrityViolationException(new DataIntegrityViolationException(""), null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals("INTERNAL SERVER ERROR", actual.getBody().getTitle());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), actual.getBody().getStatus());
    }

    @Test
    void handleDataIntegrityViolationException2() {
        ResponseEntity<ProblemJson> actual = errorHandler.handleDataIntegrityViolationException(new DataIntegrityViolationException("", new ConstraintViolationException("A REFERENCES B", new SQLException("foreign key", "23503"), "A REFERENCES B")), null);
        assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(ErrorHandler.CONFLICT_RESPONSE_TITLE, actual.getBody().getTitle());
        assertEquals(HttpStatus.CONFLICT.value(), actual.getBody().getStatus());
    }

    @Test
    void handleDataIntegrityViolationException3() {
        var exception = Mockito.mock(DataIntegrityViolationException.class);
        when(exception.getCause()).thenReturn(new ConstraintViolationException("A REFERENCES B", new SQLException("foreign key", "2292", 2292), "A REFERENCES B"));
        ResponseEntity<ProblemJson> actual = errorHandler.handleDataIntegrityViolationException(exception, null);
        assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(ErrorHandler.CONFLICT_RESPONSE_TITLE, actual.getBody().getTitle());
        assertEquals(HttpStatus.CONFLICT.value(), actual.getBody().getStatus());
    }

    @Test
    void handleDataIntegrityViolationException4() {
      ResponseEntity<ProblemJson> actual = errorHandler.handleDataIntegrityViolationException(new DataIntegrityViolationException("", new ConstraintViolationException("A REFERENCES B", new SQLException("foreign key", "23505"), "A REFERENCES B")), null);
      assertEquals(HttpStatus.CONFLICT, actual.getStatusCode());
      assertNotNull(actual.getBody());
      assertEquals(ErrorHandler.CONFLICT_RESPONSE_TITLE, actual.getBody().getTitle());
      assertEquals(HttpStatus.CONFLICT.value(), actual.getBody().getStatus());
    }

    @Test
    void handleTypeMismatch() {
        MethodParameter methodParameter = Mockito.mock(MethodParameter.class);
        ResponseEntity<Object> actual = errorHandler.handleTypeMismatch(new MethodArgumentTypeMismatchException("2", String.class, "age", methodParameter, new IllegalArgumentException("")), null, null, null);
        assertNotNull(actual.getBody());
        ProblemJson body = (ProblemJson) actual.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
        assertEquals("BAD REQUEST", body.getTitle());
        assertEquals("Invalid value 2 for property age", body.getDetail());
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.getStatus());
    }
}
