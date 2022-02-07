package it.pagopa.pagopa.apiconfig.repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.util.Optional;

@Repository
public class HealthCheckRepository {

    @Autowired
    EntityManager entityManager;

    public Optional<Object> health() {
        return Optional.of(entityManager.createNativeQuery("select 1 from DUAL").getSingleResult());
    }
}
