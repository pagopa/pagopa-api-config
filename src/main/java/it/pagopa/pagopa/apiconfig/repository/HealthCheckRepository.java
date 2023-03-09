package it.pagopa.pagopa.apiconfig.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

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
