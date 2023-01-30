package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CanaleTipoVersamentoRepository extends JpaRepository<CanaleTipoVersamento, Long> {

    Optional<CanaleTipoVersamento> findByFkCanaleAndFkTipoVersamento(Long fkCanale, Long fkTipoVersamento);
    void deleteAllByFkCanaleAndFkTipoVersamento(Long fkCanale, Long fkTipoVersamento);

    List<CanaleTipoVersamento> findByFkCanale(Long fkCanale);
}
