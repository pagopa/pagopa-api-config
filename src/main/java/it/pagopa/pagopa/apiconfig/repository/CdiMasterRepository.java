package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CdiMasterRepository extends JpaRepository<CdiMaster, Long> {

    Optional<CdiMaster> findByIdInformativaPsp(String idCdi);
}
