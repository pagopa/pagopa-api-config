package it.pagopa.pagopa.apiconfig;

import it.pagopa.pagopa.apiconfig.config.StorageProperties;
import it.pagopa.pagopa.apiconfig.config.XSDProperties;
import it.pagopa.pagopa.apiconfig.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Locale;

@SpringBootApplication
@EnableJpaRepositories
@EnableRetry
@EnableConfigurationProperties({StorageProperties.class, XSDProperties.class})
public class ApiConfig {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(ApiConfig.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return args -> storageService.init();
    }
}
