package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class CanaliSpecification {

  private CanaliSpecification() {}

  public static Specification<Canali> filterChannelsByCodeAndBrokerDescriptionFilters(
      String brokerCode, String brokerDescription, String channelId) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> predicates = new ArrayList<>();

      Join<Canali, IntermediariPsp> canaliIntermediariPspJoin =
          root.join("fkIntermediarioPsp", JoinType.LEFT);

      if (StringUtils.isNotEmpty(brokerCode)) {
        predicates.add(
            cb.and(cb.equal(canaliIntermediariPspJoin.get("idIntermediarioPsp"), brokerCode)));
      }

      if (StringUtils.isNotEmpty(channelId)) {
        predicates.add(
            cb.and(cb.like(cb.lower(root.get("idCanale")), "%" + channelId.toLowerCase() + "%")));
      }

      if (StringUtils.isNotEmpty(brokerDescription)) {
        predicates.add(
            cb.and(
                cb.like(
                    cb.lower(canaliIntermediariPspJoin.get("codiceIntermediario")),
                    "%" + brokerDescription.toLowerCase() + "%")));
      }

      Predicate[] rawPredicates = new Predicate[predicates.size()];
      return cb.and(predicates.toArray(rawPredicates));
    };
  }
}
