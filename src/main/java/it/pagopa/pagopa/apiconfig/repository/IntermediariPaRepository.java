package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntermediariPaRepository extends JpaRepository<IntermediariPa, Long> {

    Optional<IntermediariPa> findByIdIntermediarioPa(String brokerCode);
}
