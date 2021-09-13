package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.models.entities.Pa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaRepository extends JpaRepository<Pa, Long> {
}