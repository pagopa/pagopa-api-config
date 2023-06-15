package it.gov.pagopa.apiconfig.core.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
                null, null, null, null, null, null, null)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaStazionePaSpecification.filterViewPaBrokerStation(
                "123", "123", "123", 1L, 1L, 1L, Boolean.TRUE)
            .toPredicate(root, query, builder);
    assertNotNull(p);
  }
}
