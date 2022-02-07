package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.repository.HealthCheckRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class HealthCheckServiceTest {

    @MockBean
    private HealthCheckRepository healthCheckRepository;

    @Autowired
    @InjectMocks
    private HealthCheckService healthCheckService;

    @Test
    void getDBConnection_up() {
        when(healthCheckRepository.findBy()).thenReturn(Optional.of(true));

        boolean actual = healthCheckService.checkDatabaseConnection();
        assertTrue(actual);
    }

    @Test
    void getDBConnection_down() {
        when(healthCheckRepository.findBy()).thenReturn(Optional.empty());

        boolean actual = healthCheckService.checkDatabaseConnection();
        assertFalse(actual);
    }

}
