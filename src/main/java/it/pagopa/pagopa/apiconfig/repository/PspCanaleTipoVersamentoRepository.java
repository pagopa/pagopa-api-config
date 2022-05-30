package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PspCanaleTipoVersamentoRepository extends JpaRepository<PspCanaleTipoVersamento, Long> {

    List<PspCanaleTipoVersamento> findByFkPsp(Long pspId);

    Optional<PspCanaleTipoVersamento> findByFkPspAndCanaleTipoVersamento_FkCanaleAndCanaleTipoVersamento_FkTipoVersamento(Long idPsp, Long idChannel, Long idPaymentType);

    Optional<PspCanaleTipoVersamento> findByFkPspAndCanaleTipoVersamento_CanaleIdCanaleAndCanaleTipoVersamento_TipoVersamentoTipoVersamento(Long idPsp, String channel, String paymentType);

    List<PspCanaleTipoVersamento> findByFkPspAndCanaleTipoVersamento_FkCanale(Long idPsp, Long idChannel);

    List<PspCanaleTipoVersamento> findByCanaleTipoVersamento_Canale_IdCanale(String idChannel);
}
