package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    @Autowired
    HealthCheckRepository healthCheckRepository;

    public Boolean checkDatabaseConnection() {
        return healthCheckRepository.findBy().isPresent();
    }
}
