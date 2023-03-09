package it.pagopa.pagopa.apiconfig.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class HealthCheckRepository {

    @Autowired
    EntityManager entityManager;

    @Value("${healthcheck.query}")
    private String query;

    public Optional<Object> health() {
        return Optional.of(entityManager.createNativeQuery(query).getSingleResult());
    }
}
