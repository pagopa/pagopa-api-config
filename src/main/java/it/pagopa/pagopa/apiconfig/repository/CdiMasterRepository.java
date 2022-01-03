package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CdiMasterRepository extends PagingAndSortingRepository<CdiMaster, Long> {

    Optional<CdiMaster> findByIdInformativaPspAndFkPsp_IdPsp(String idCdi, String pspCode);
}
