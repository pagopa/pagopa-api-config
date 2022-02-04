package it.pagopa.pagopa.apiconfig.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Optional;

interface CustomNativeRepository {
    Optional<Object> findBy();
}

@Repository
public class HealthCheckRepository implements CustomNativeRepository {

    @Autowired
    EntityManager entityManager;

    @Override
    public Optional<Object> findBy() {
        return Optional.of(entityManager.createNativeQuery("select 1 from DUAL").getSingleResult());
    }
}
