package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before(value = "@within(org.springframework.web.bind.annotation.RestController) && args(someArg)", argNames = "someArgs")
    public void logApiInvocation(JoinPoint joinPoint) {
        log.info("Invoking API operation: {} args={}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "@within(org.springframework.web.bind.annotation.RestController)", returning = "result")
    public void returnApiInvocation(JoinPoint joinPoint, Object result) {
        log.info("Successful API operation: {} result={}", joinPoint.getSignature().getName(), result);
    }
}
