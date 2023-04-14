package it.gov.pagopa.apiconfig.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;

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
