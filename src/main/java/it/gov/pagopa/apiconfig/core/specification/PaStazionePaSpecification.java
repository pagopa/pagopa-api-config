package it.gov.pagopa.apiconfig.core.specification;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;

public class PaStazionePaSpecification {
  
  private PaStazionePaSpecification() {}
  
  public static Specification<PaStazionePa> filterViewPaBrokerStation(String creditorInstitutionCode,
      String paBrokerCode, String stationCode, Long auxDigit, Long progressivo, Long codiceSegregazione, Boolean quartoModello) {
    return (root, query, cb) -> {
      query.distinct(true);
      List<Predicate> list = new ArrayList<>();
      
      Join<PaStazionePa, Pa> pa =
          root.join("pa", JoinType.LEFT); 
      Join<PaStazionePa, Stazioni> stazioni =
          root.join("fkStazione", JoinType.LEFT);
      Join<Stazioni, IntermediariPa> intermediariPa =
          stazioni.join("intermediarioPa", JoinType.LEFT);
      
      if (null != auxDigit) {
        list.add(cb.and(cb.equal(root.get("auxDigit"), auxDigit)));
      }
      if (null != progressivo) {
        list.add(cb.and(cb.equal(root.get("progressivo"), progressivo)));
      }
      if (null != codiceSegregazione) {
        list.add(cb.and(cb.equal(root.get("segregazione"), codiceSegregazione)));
      }
      if (null != quartoModello) {
        list.add(cb.and(cb.equal(root.get("quartoModello"), quartoModello)));
      }
      if (StringUtils.isNotEmpty(creditorInstitutionCode)) {
        list.add(cb.and(cb.equal(pa.get("idDominio"), creditorInstitutionCode)));
      }
      if (StringUtils.isNotEmpty(stationCode)) {
        list.add(cb.and(cb.equal(stazioni.get("idStazione"), stationCode)));
      }
      if (StringUtils.isNotEmpty(paBrokerCode)) {
        list.add(cb.and(cb.equal(intermediariPa.get("idIntermediarioPa"), paBrokerCode)));
      }
      Predicate[] p = new Predicate[list.size()];
      return cb.and(list.toArray(p));
    };
  }
}
