package it.pagopa.pagopa.apiconfig.config;

import it.pagopa.pagopa.apiconfig.cosmos.repository.CdiCosmosRepository;
import it.pagopa.pagopa.apiconfig.cosmos.repository.PaymentTypesCosmosRepository;
import it.pagopa.pagopa.apiconfig.redis.repository.RedisRepository;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockitoConfig {

    @Bean
    @Primary
    public CdiCosmosRepository cdiCosmosRepository() {
        return Mockito.mock(CdiCosmosRepository.class);
    }

    @Bean
    @Primary
    public PaymentTypesCosmosRepository paymentTypesCosmosRepository() { return Mockito.mock(PaymentTypesCosmosRepository.class); }

    @Bean
    @Primary
    public RedisRepository redisRepository() { return Mockito.mock(RedisRepository.class); }


}
