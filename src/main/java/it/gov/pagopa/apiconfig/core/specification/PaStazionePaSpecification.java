package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class PaStazionePaSpecification {

  private PaStazionePaSpecification() {}

  public static Specification<PaStazionePa> filterByStationAndCreditorInstitution(Long stationObjId, String filterByCiNameOrCF) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> list = new ArrayList<>();

      Join<PaStazionePa, Pa> pa = root.join("pa", JoinType.LEFT);
      Join<PaStazionePa, Stazioni> stazioni = root.join("fkStazione", JoinType.LEFT);

      if (StringUtils.isNotEmpty(filterByCiNameOrCF)) {
        var conditionName = cb.like(cb.lower(pa.get("ragioneSociale")), "%" + filterByCiNameOrCF.toLowerCase() + "%");
        var conditionCF = cb.like(cb.lower(pa.get("idDominio")), "%" + filterByCiNameOrCF.toLowerCase() + "%");
        var condition = cb.or(conditionCF, conditionName);
        list.add(cb.and(condition));
      }
      if (stationObjId != null) {
        list.add(cb.and(cb.equal(stazioni.get("objId"), stationObjId)));
      }
      Predicate[] p = new Predicate[list.size()];
      return cb.and(list.toArray(p));
    };
  }

  public static Specification<PaStazionePa> filterViewPaBrokerStation(
      String creditorInstitutionCode,
      String paBrokerCode,
      String stationCode,
      Boolean enabled,
      Long auxDigit,
      Long applicationCode,
      Long segregationCode,
      Boolean mod4) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> list = new ArrayList<>();

      Join<PaStazionePa, Pa> pa = root.join("pa", JoinType.LEFT);
      Join<PaStazionePa, Stazioni> stazioni = root.join("fkStazione", JoinType.LEFT);
      Join<Stazioni, IntermediariPa> intermediariPa =
          stazioni.join("intermediarioPa", JoinType.LEFT);

      if (null != auxDigit) {
        list.add(cb.and(cb.equal(root.get("auxDigit"), auxDigit)));
      }
      if (null != applicationCode) {
        list.add(cb.and(cb.equal(root.get("progressivo"), applicationCode)));
      }
      if (null != segregationCode) {
        list.add(cb.and(cb.equal(root.get("segregazione"), segregationCode)));
      }
      if (null != mod4) {
        list.add(cb.and(cb.equal(root.get("quartoModello"), mod4)));
      }
      if (StringUtils.isNotEmpty(creditorInstitutionCode)) {
        list.add(cb.and(cb.equal(pa.get("idDominio"), creditorInstitutionCode)));
      }
      if (StringUtils.isNotEmpty(stationCode)) {
        list.add(cb.and(cb.equal(stazioni.get("idStazione"), stationCode)));
      }
      if (null != enabled) {
        list.add(cb.and(cb.equal(stazioni.get("enabled"), enabled)));
      }
      if (StringUtils.isNotEmpty(paBrokerCode)) {
        list.add(cb.and(cb.equal(intermediariPa.get("idIntermediarioPa"), paBrokerCode)));
      }
      Predicate[] p = new Predicate[list.size()];
      return cb.and(list.toArray(p));
    };
  }
}
