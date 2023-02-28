package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@SuppressWarnings(
    "java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PspRepository extends JpaRepository<Psp, Long> {

  Optional<Psp> findByIdPsp(String id);

  Page<Psp>
      findAllByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_fkIntermediarioPsp_idIntermediarioPsp(
          String brokerCode, Pageable pageable);
  Page<Psp> findDistinctByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_idCanale(String channelCode, Pageable pageable);
}
