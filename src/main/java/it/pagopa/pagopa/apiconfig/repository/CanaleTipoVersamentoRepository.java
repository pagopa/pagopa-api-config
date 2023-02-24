package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings(
    "java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CanaleTipoVersamentoRepository extends JpaRepository<CanaleTipoVersamento, Long> {

  Optional<CanaleTipoVersamento> findByFkCanaleAndFkTipoVersamento(
      Long fkCanale, Long fkTipoVersamento);

  List<CanaleTipoVersamento> findByFkCanale(Long fkCanale);

  List<CanaleTipoVersamento> findByCanale_fkIntermediarioPsp_idIntermediarioPsp(
      String brokerPspCode);
}
