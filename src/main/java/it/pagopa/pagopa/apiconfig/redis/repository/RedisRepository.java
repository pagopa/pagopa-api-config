package it.pagopa.pagopa.apiconfig.redis.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;


@Component
public class RedisRepository {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object value, long ttl) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttl));
    }
}
