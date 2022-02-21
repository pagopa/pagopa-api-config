package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CdiMasterRepository extends JpaRepository<CdiMaster, Long> {

    Optional<CdiMaster> findByIdInformativaPspAndFkPsp_IdPsp(String idCdi, String pspCode);

    List<CdiMaster> findByFkPsp_IdPspAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(String idDominio, Timestamp now);

}
