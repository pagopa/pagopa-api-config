package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.model.psp.BrokersPsp;
import it.pagopa.pagopa.apiconfig.repository.HealthCheckRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class HealthCheckServiceTest {

    @MockBean
    private HealthCheckRepository healthCheckRepository;

    @Autowired
    @InjectMocks
    private HealthCheckService healthCheckService;

    @Test
    void getDBConnection() {
        when(healthCheckRepository.findBy()).thenReturn(Optional.of(true));

        boolean actual = healthCheckService.checkDatabaseConnection();
        assertTrue(actual);
    }

}
