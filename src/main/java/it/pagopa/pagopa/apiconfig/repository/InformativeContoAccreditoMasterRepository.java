package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface InformativeContoAccreditoMasterRepository extends PagingAndSortingRepository<InformativeContoAccreditoMaster, Long> {

    Optional<InformativeContoAccreditoMaster> findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(String idIca, String creditorInstitutionCode);

}
