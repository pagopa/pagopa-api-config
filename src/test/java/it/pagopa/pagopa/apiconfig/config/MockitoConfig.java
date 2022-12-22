package it.pagopa.pagopa.apiconfig.config;

import it.pagopa.pagopa.apiconfig.repository.CdiCosmosRepository;
import it.pagopa.pagopa.apiconfig.repository.PaymentTypesCosmosRepository;
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


}
