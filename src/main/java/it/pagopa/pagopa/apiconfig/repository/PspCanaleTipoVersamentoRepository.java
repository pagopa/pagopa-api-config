package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PspCanaleTipoVersamentoRepository extends JpaRepository<PspCanaleTipoVersamento, Long> {

    List<PspCanaleTipoVersamento> findByFkPsp(Long pspId);
}
