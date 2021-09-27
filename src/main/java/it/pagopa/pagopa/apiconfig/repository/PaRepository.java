package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaRepository extends PagingAndSortingRepository<Pa, Long> {

    Optional<Pa> findByIdDominio(String creditorInstitutionCode);
}
