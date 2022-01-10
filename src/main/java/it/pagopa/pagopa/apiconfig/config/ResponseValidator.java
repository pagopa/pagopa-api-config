package it.pagopa.pagopa.apiconfig.config;

import it.pagopa.pagopa.apiconfig.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class ResponseValidator {

    @Autowired
    private Validator validator;


    /**
     * This method validates the response annotated with the {@link javax.validation.constraints}
     *
     * @param joinPoint not used
     * @param result the response to validate
     */
    @AfterReturning(pointcut = "execution(* it.pagopa.pagopa.apiconfig.controller.*.*(..))", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        validateResponse(result);
    }

    private void validateResponse(Object object) {
        Set<ConstraintViolation<Object>> validationResults = validator.validate(object);

        if (!validationResults.isEmpty()) {
            var sb = new StringBuilder();
            for (ConstraintViolation<Object> error : validationResults) {
                sb.append(error.getPropertyPath()).append(" - ").append(error.getMessage()).append("\n");
            }
            var msg = sb.toString();
            log.warn(msg);
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid response", msg);
        }
    }
}
