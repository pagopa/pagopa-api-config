package it.gov.pagopa.apiconfig.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${cache.enabled}'=='true'")
@EnableCaching
public class CacheConfig {}
