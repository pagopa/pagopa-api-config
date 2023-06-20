package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HealthCheckService {

  @Autowired HealthCheckRepository healthCheckRepository;

  public boolean checkDatabaseConnection() {
    try {
      return healthCheckRepository.health().isPresent();
    } catch (DataAccessResourceFailureException e) {
      return false;
    }
  }
}
