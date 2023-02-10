package it.pagopa.pagopa.apiconfig.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories("it.pagopa.pagopa.apiconfig.redis.repository")
//@ConditionalOnExpression("'${redis.enable}' == 'true'")
public class RedisConfig {

  @Value("${spring.redis.password}")
  private String redisPassword;

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
    redisConfiguration.setPassword(redisPassword);
    return new LettuceConnectionFactory(redisConfiguration);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(final LettuceConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setKeySerializer(new StringRedisSerializer());

    final var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    final var objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.ALL,  JsonAutoDetect.Visibility.ANY);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setConnectionFactory(connectionFactory);

    return template;
  }

}
