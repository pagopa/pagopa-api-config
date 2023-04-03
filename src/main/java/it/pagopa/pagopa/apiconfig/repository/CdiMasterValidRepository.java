package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CdiMasterValid;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings(
    "java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CdiMasterValidRepository extends PagingAndSortingRepository<CdiMasterValid, Long> {

  Page<CdiMasterValid> findAll(Pageable pageable);

  Optional<CdiMasterValid> findByIdInformativaPspAndFkPsp_IdPsp(String idCdi, String pspCode);

  List<CdiMasterValid> findByFkPsp_IdPspAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(
      String idDominio, Timestamp now);
}
