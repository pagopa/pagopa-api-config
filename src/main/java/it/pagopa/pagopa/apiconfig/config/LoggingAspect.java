package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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

    @Value("${application.name}")
    private String name;

    @Value("${application.version}")
    private String version;

    @Value("${properties.environment}")
    private String environment;


    /**
     * Log essential info of application during the startup.
     */
    @PostConstruct
    public void logStartup() {
        log.info("-> Starting {} version {} - environment {}", name, version, environment);
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
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
                .forEach(prop -> log.debug("{}: {}", prop, env.getProperty(prop)));
    }

    @Before(value = "@within(org.springframework.web.bind.annotation.RestController)")
    public void logApiInvocation(JoinPoint joinPoint) {
        log.info("Invoking API operation: {} args={}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "@within(org.springframework.web.bind.annotation.RestController)", returning = "result")
    public void returnApiInvocation(JoinPoint joinPoint, Object result) {
        log.info("Successful API operation: {} result={}", joinPoint.getSignature().getName(), result);
    }
}
