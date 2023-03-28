package it.pagopa.pagopa.apiconfig.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import it.pagopa.pagopa.apiconfig.entity.Psp;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PspRepository extends JpaRepository<Psp, Long> {
  
  final String PSP_FIELDS = "psp0_.obj_id as obj_id1_27_, psp0_.abi as abi2_27_, psp0_.agid_psp as agid_psp3_27_, psp0_.bic as bic4_27_, psp0_.codice_fiscale as codice_f5_27_, psp0_.codice_mybank as codice_m6_27_, psp0_.descrizione as descrizi7_27_, psp0_.email_repo_commissione_carico_pa as email_re8_27_, psp0_.enabled as enabled9_27_, psp0_.fk_int_quadrature as fk_int_18_27_, psp0_.flag_repo_commissione_carico_pa as flag_re10_27_, psp0_.id_psp as id_psp11_27_, psp0_.marca_bollo_digitale as marca_b12_27_, psp0_.psp_avv as psp_avv13_27_, psp0_.psp_nodo as psp_nod14_27_, psp0_.ragione_sociale as ragione15_27_, psp0_.storno_pagamento as storno_16_27_, psp0_.vat_number as vat_num17_27_";

  Optional<Psp> findByIdPsp(String id);

  Page<Psp> findAllByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_fkIntermediarioPsp_idIntermediarioPsp(
      String brokerCode, Pageable pageable);

  Page<Psp> findDistinctByPspCanaleTipoVersamentoList_canaleTipoVersamento_canale_idCanale(String channelCode,
      Pageable pageable);

  @Query(value = "SELECT "+PSP_FIELDS+" FROM NODO4_CFG.Psp psp0_ "
      + "LEFT OUTER JOIN NODO4_CFG.Psp_Canale_Tipo_Versamento pspcanalet1_ on psp0_.obj_id=pspcanalet1_.fk_psp "
      + "LEFT OUTER JOIN NODO4_CFG.Canale_Tipo_Versamento canaletipo2_     on pspcanalet1_.fk_canale_tipo_versamento=canaletipo2_.obj_id "
      + "LEFT OUTER JOIN NODO4_CFG.Canali canali3_                         on canaletipo2_.fk_canale=canali3_.obj_id "
      + "WHERE (canali3_.id_canale = :channelCode) "
      + "AND   (:pspCode is null OR psp0_.id_psp = :pspCode) "
      + "AND   (:pspName is null OR LOWER(psp0_.ragione_sociale) LIKE LOWER(CONCAT('%',:pspName,'%'))) "
      + "AND   (:pspEnabled is null OR psp0_.enabled = :pspEnabled)"
      , nativeQuery = true)
  Page<Psp> findDistinctPspByChannel(String channelCode, String pspCode, String pspName, String pspEnabled,Pageable pageable);
}
