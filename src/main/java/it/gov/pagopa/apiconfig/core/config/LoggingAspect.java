package it.gov.pagopa.apiconfig.core.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.StreamSupport;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  @Value("${info.application.artifactId}")
  private String artifactId;

  @Value("${info.application.version}")
  private String version;

  @Value("${info.properties.environment}")
  private String environment;

  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void restController() {
    // all rest controllers
  }

  @Pointcut("execution(* it.gov.pagopa.apiconfig.starter.repository..*.*(..))")
  public void repository() {
    // all repository methods
  }

  @Pointcut("execution(* it.gov.pagopa.apiconfig.core.service..*.*(..))")
  public void service() {
    // all service methods
  }

  @Pointcut("execution(* it.gov.pagopa.apiconfig.core.mapper..*.*(..))")
  public void mapper() {
    // all mapper methods
  }

  @Pointcut("execution(* it.gov.pagopa.apiconfig.core.util..*.*(..))")
  public void util() {
    // all util methods
  }

  @Pointcut("execution(* it.gov.pagopa.apiconfig.core.client..*.*(..))")
  public void client() {
    // all client methods
  }

  /** Log essential info of application during the startup. */
  @PostConstruct
  public void logStartup() {
    log.info("-> Starting {} version {} - environment {}", artifactId, version, environment);
  }

  /**
   * If DEBUG log-level is enabled prints the env variables and the application properties.
   *
   * @param event Context of application
   */
  @EventListener
  public void handleContextRefresh(ContextRefreshedEvent event) {
    final Environment env = event.getApplicationContext().getEnvironment();
    log.debug("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
    final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
    StreamSupport.stream(sources.spliterator(), false)
        .filter(EnumerablePropertySource.class::isInstance)
        .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
        .flatMap(Arrays::stream)
        .distinct()
        .filter(
            prop ->
                !(prop.toLowerCase().contains("credentials")
                    || prop.toLowerCase().contains("password")
                    || prop.toLowerCase().contains("pass")
                    || prop.toLowerCase().contains("pwd")))
        .forEach(prop -> log.debug("{}: {}", prop, env.getProperty(prop)));
  }

  @Before(value = "restController()")
  public void logApiInvocation(JoinPoint joinPoint) {
    log.info(
        "Invoking API operation {} - args: {}",
        joinPoint.getSignature().getName(),
        joinPoint.getArgs());
  }

  @AfterReturning(value = "restController()", returning = "result")
  public void returnApiInvocation(JoinPoint joinPoint, Object result) {
//    log.info(
//        "Successful API operation {} - result: {}", joinPoint.getSignature().getName(), result);
  }

  @AfterReturning(
      value = "execution(* it.gov.pagopa.apiconfig.core.exception.ErrorHandler.*(..))",
      returning = "result")
  public void trowingApiInvocation(JoinPoint joinPoint, Object result) {
    log.info("Failed API operation {} - error: {}", joinPoint.getSignature().getName(), result);
  }

  @Around(value = "repository() || service()")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    long endTime = System.currentTimeMillis();
    log.trace(
        "Time taken for Execution of {} is: {}ms",
        joinPoint.getSignature().toShortString(),
        (endTime - startTime));
    return result;
  }

  @Around(value = "repository() || service() || mapper() || util() || client()")
  public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug(
        "Call method {} - args: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    Object result = joinPoint.proceed();
    log.debug("Return method {} - result: {}", joinPoint.getSignature().toShortString(), result);
    return result;
  }
}
