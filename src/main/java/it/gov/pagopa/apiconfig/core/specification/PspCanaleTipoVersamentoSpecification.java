package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CanaliNodo;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class PspCanaleTipoVersamentoSpecification {

  private PspCanaleTipoVersamentoSpecification() {}

  public static Specification<PspCanaleTipoVersamento> filterViewPspChannelBroker(
      String pspCode,
      String pspBrokerCode,
      String channelCode,
      String paymentType,
      String paymentMethod) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> list = new ArrayList<>();

      Join<PspCanaleTipoVersamento, Psp> psp = root.join("psp", JoinType.LEFT);
      Join<PspCanaleTipoVersamento, CanaleTipoVersamento> canaleTipoVersamento =
          root.join("canaleTipoVersamento", JoinType.LEFT);
      Join<CanaleTipoVersamento, Canali> canali =
          canaleTipoVersamento.join("canale", JoinType.LEFT);
      Join<CanaleTipoVersamento, TipiVersamento> tipiVersamento =
          canaleTipoVersamento.join("tipoVersamento", JoinType.LEFT);
      Join<Canali, IntermediariPsp> intermediariPsp =
          canali.join("fkIntermediarioPsp", JoinType.LEFT);
      Join<Canali, CanaliNodo> canaliNodo = canali.join("fkCanaliNodo", JoinType.LEFT);

      if (StringUtils.isNotEmpty(pspCode)) {
        list.add(cb.and(cb.equal(psp.get("idPsp"), pspCode)));
      }
      if (StringUtils.isNotEmpty(pspBrokerCode)) {
        list.add(cb.and(cb.equal(intermediariPsp.get("idIntermediarioPsp"), pspBrokerCode)));
      }
      if (StringUtils.isNotEmpty(channelCode)) {
        list.add(cb.and(cb.equal(canali.get("idCanale"), channelCode)));
      }
      if (StringUtils.isNotEmpty(paymentType)) {
        list.add(cb.and(cb.equal(tipiVersamento.get("tipoVersamento"), paymentType)));
      }
      if (StringUtils.isNotEmpty(paymentMethod)) {
        list.add(cb.and(cb.equal(canaliNodo.get("modelloPagamento"), paymentMethod)));
      }
      Predicate[] p = new Predicate[list.size()];
      return cb.and(list.toArray(p));
    };
  }
}
