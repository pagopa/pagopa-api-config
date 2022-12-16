package it.pagopa.pagopa.apiconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Locale;


@SpringBootApplication
@EnableJpaRepositories
@EnableRetry
public class ApiConfig {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        SpringApplication.run(ApiConfig.class, args);
    }
}

