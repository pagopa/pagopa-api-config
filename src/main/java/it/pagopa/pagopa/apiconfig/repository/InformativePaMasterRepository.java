package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformativePaMasterRepository extends PagingAndSortingRepository<InformativePaMaster, Long> {

    Optional<InformativePaMaster> findByIdInformativaPaAndFkPa_IdDominio(String idCounterpartTable, String creditorInstitutionCode);

}
