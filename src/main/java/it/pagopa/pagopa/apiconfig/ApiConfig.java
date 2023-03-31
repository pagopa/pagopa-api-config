package it.pagopa.pagopa.apiconfig;

import java.util.Locale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableJpaRepositories("it.gov.pagopa.apiconfig.starter.repository")
@EntityScan("it.gov.pagopa.apiconfig.starter.entity")
@EnableRetry
public class ApiConfig {

  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    SpringApplication.run(ApiConfig.class, args);
  }
}
