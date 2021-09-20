package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaRepository extends JpaRepository<Pa, Long> {

    Optional<Pa> findByIdDominio(String idDominio);
}