package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Aspect
@Component
@Slf4j
public class ResponseValidator {

    @Autowired
    private Validator validator;

    @Autowired
    private HttpServletResponse response;

    /**
     * This method validates the response annotated with the {@link javax.validation.constraints}
     *
     * @param joinPoint not used
     * @param result    the response to validate
     */
    @AfterReturning(pointcut = "execution(* it.pagopa.pagopa.apiconfig.controller.*.*(..))", returning = "result")
    public void validateResponse(JoinPoint joinPoint, Object result) {
        if (result instanceof ResponseEntity) {
            validateResponse((ResponseEntity<?>) result);
        }
    }

    private <T> void validateResponse(ResponseEntity<T> responseEntity) {
        if (responseEntity.getBody() != null) {
            Set<ConstraintViolation<T>> validationResults = validator.validate(responseEntity.getBody());

            if (!validationResults.isEmpty()) {
                var sb = new StringBuilder();
                for (ConstraintViolation<T> error : validationResults) {
                    sb.append(error.getPropertyPath()).append(" ").append(error.getMessage()).append(". ");
                }
                var msg = StringUtils.chop(sb.toString());
                log.warn("Invalid Response: {}", msg);
                response.setHeader("X-Warning", msg);
            }
        }
    }
}
