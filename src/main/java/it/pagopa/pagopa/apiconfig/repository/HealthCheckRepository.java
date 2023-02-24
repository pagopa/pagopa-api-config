package it.pagopa.pagopa.apiconfig.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HealthCheckRepository {

  @Autowired EntityManager entityManager;

  public Optional<Object> health() {
    return Optional.of(entityManager.createNativeQuery("select 1 from DUAL").getSingleResult());
  }
}
