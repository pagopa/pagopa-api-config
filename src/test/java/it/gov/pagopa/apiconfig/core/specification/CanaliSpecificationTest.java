package it.gov.pagopa.apiconfig.core.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
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
class CanaliSpecificationTest {

  Root<Canali> root;

  CriteriaQuery<?> query;

  CriteriaBuilder builder;

  @Autowired EntityManager em;

  @BeforeAll
  public void setUp() {
    root = em.getCriteriaBuilder().createQuery().from(Canali.class);
    query = em.getCriteriaBuilder().createQuery();
    builder = em.getCriteriaBuilder();
  }

  @Test
  void createQuerySpecification() {
    Predicate p =
        CanaliSpecification.filterChannelsByCodeAndBrokerDescriptionFilters(null, null, null)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        CanaliSpecification.filterChannelsByCodeAndBrokerDescriptionFilters("123", "123", "123")
            .toPredicate(root, query, builder);
    assertNotNull(p);
  }
}
