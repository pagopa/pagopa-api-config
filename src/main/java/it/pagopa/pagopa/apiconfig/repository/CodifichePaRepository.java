package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodifichePaRepository extends JpaRepository<CodifichePa, Long> {

    List<CodifichePa> findAllByCodicePa(String creditorInstitutionCode);
}
