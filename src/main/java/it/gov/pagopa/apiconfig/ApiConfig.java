package it.gov.pagopa.apiconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Locale;

@SpringBootApplication
@EnableRetry
public class ApiConfig {

  public static void main(String[] args) {
    Locale.setDefault(Locale.ENGLISH);
    SpringApplication.run(ApiConfig.class, args);
  }
}
