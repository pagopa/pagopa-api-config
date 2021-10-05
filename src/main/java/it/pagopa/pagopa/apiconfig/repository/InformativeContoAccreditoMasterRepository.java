package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformativeContoAccreditoMasterRepository extends PagingAndSortingRepository<InformativeContoAccreditoMaster, Long> {

    Optional<InformativeContoAccreditoMaster> findByIdInformativaContoAccreditoPa(String idIca);

}
