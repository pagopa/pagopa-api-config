package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntermediariPspRepository extends JpaRepository<IntermediariPsp, Long> {

  Optional<IntermediariPsp> findByIdIntermediarioPsp(String brokerPspCode);
}
