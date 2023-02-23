package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings(
    "java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CanaliRepository extends JpaRepository<Canali, Long> {

  Optional<Canali> findByIdCanale(String idCanale);

  List<Canali> findByFkIntermediarioPsp_idIntermediarioPsp(String brokerCode);
}
