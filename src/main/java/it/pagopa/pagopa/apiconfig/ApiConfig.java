package it.pagopa.pagopa.apiconfig;

import it.pagopa.pagopa.apiconfig.repository.CdiCosmosRepository;
import it.pagopa.pagopa.apiconfig.repository.PaymentTypesCosmosRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

@SpringBootApplication
@EnableJpaRepositories(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                CdiCosmosRepository.class, PaymentTypesCosmosRepository.class}))
@EnableRetry
public class ApiConfig {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(ApiConfig.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

