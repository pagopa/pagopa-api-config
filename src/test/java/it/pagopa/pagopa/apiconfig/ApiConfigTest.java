package it.pagopa.pagopa.apiconfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ApiConfigTest {

  @Test
  void applicationContextLoaded() {
    assertTrue(true); // it just tests that an error has not occurred
  }

  @Test
  void applicationContextTest() {
    ApiConfig.main(new String[] {});
    assertTrue(true); // it just tests that an error has not occurred
  }
}
