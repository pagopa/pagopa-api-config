package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanaleTipoVersamentoRepository extends JpaRepository<CanaleTipoVersamento, Long> {
}
