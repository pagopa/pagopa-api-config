package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class PspSpecification {

  private PspSpecification() {}

  public static Specification<Psp> filterPspByChannelIdAndOptionalFilters(
      String channelCode, String pspCode, String pspName, Boolean pspEnabled) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> list = new ArrayList<>();
      Join<Psp, PspCanaleTipoVersamento> pspCanaleTipoVersamento =
          root.join("pspCanaleTipoVersamentoList", JoinType.LEFT);
      Join<PspCanaleTipoVersamento, CanaleTipoVersamento> canaleTipoVersamento =
          pspCanaleTipoVersamento.join("canaleTipoVersamento", JoinType.LEFT);
      Join<CanaleTipoVersamento, Canali> canali =
          canaleTipoVersamento.join("canale", JoinType.LEFT);

      list.add(cb.equal(canali.get("idCanale"), channelCode));
      if (StringUtils.isNotEmpty(pspCode)) {
        list.add(cb.and(cb.equal(root.get("idPsp"), pspCode)));
      }
      if (StringUtils.isNotEmpty(pspName)) {
        list.add(
            cb.and(
                cb.like(cb.lower(root.get("ragioneSociale")), "%" + pspName.toLowerCase() + "%")));
      }
      if (null != pspEnabled) {
        list.add(cb.and(cb.equal(root.get("enabled"), pspEnabled)));
      }

      Predicate[] p = new Predicate[list.size()];
      return cb.and(list.toArray(p));
    };
  }
}
