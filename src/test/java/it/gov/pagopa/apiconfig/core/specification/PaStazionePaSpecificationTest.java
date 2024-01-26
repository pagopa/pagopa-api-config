package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ApiConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class PaStazionePaSpecificationTest {

  Root<PaStazionePa> root;

  CriteriaQuery<?> query;

  CriteriaBuilder builder;

  @Autowired EntityManager em;

  @BeforeAll
  public void setUp() {
    root = em.getCriteriaBuilder().createQuery().from(PaStazionePa.class);
    query = em.getCriteriaBuilder().createQuery();
    builder = em.getCriteriaBuilder();
  }

  @Test
  void createQuerySpecification() {
    Predicate p =
        PaStazionePaSpecification.filterViewPaBrokerStation(
                null, null, null, null, null, null, null, null)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaStazionePaSpecification.filterViewPaBrokerStation(
                "123", "123", "123", Boolean.TRUE, 1L, 1L, 1L, Boolean.TRUE)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
            PaStazionePaSpecification.filterByStationAndCreditorInstitution(
                            null, null, null)
                    .toPredicate(root, query, builder);
    assertNotNull(p);
    p =
            PaStazionePaSpecification.filterByStationAndCreditorInstitution(
                            123L, null, null)
                    .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
            PaStazionePaSpecification.filterByStationAndCreditorInstitution(
                            null, "Comune di", null)
                    .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
            PaStazionePaSpecification.filterByStationAndCreditorInstitution(
                            123L, "Comune di", null)
                    .toPredicate(root, query, builder);
    assertNotNull(p);
  }
}
