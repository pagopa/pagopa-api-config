package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PspRepository extends JpaRepository<Psp, Long> {

    Optional<Psp> findByIdPsp(String id);

    Page<Psp> findAllByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_fkIntermediarioPsp_idIntermediarioPsp(String brokerCode, Pageable pageable);

}
