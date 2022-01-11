package it.pagopa.pagopa.apiconfig.config;

import it.pagopa.pagopa.apiconfig.exception.AppException;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Aspect
@Component
public class ResponseValidator {

    @Autowired
    private Validator validator;


    /**
     * This method validates the response annotated with the {@link javax.validation.constraints}
     *
     * @param joinPoint not used
     * @param result    the response to validate
     */
    @AfterReturning(pointcut = "execution(* it.pagopa.pagopa.apiconfig.controller.*.*(..))", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        validateResponse((ResponseEntity<?>) result);
    }

    private <T> void validateResponse(ResponseEntity<T> response) {
        if (response.getBody() != null) {
            Set<ConstraintViolation<T>> validationResults = validator.validate(response.getBody());

            if (!validationResults.isEmpty()) {
                var sb = new StringBuilder();
                for (ConstraintViolation<T> error : validationResults) {
                    sb.append(error.getPropertyPath()).append(" ").append(error.getMessage()).append(". ");
                }
                var msg = StringUtils.chop(sb.toString());
                throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid response", msg);
            }
        }
    }
}
