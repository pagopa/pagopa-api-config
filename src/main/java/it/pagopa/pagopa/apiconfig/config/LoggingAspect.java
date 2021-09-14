package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("@within(org.springframework.web.bind.annotation.RestController)")
    public void logApiInvocation(JoinPoint joinPoint) {
      log.info("Invoking api opoeration: " + joinPoint.getSignature().getName());
    }
}
