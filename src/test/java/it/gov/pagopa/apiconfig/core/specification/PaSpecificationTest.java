package it.gov.pagopa.apiconfig.core.specification;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Filter;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ApiConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class PaSpecificationTest {

  Root<Pa> root;

  CriteriaQuery<?> query;

  CriteriaBuilder builder;

  @Autowired EntityManager em;

  @BeforeAll
  public void setUp() {
    root = em.getCriteriaBuilder().createQuery().from(Pa.class);
    query = em.getCriteriaBuilder().createQuery();
    builder = em.getCriteriaBuilder();
  }

  @Test
  void createQuerySpecification() {
    Filter filter = Filter.builder().code("testCode").build();
    Order order = Order.builder().ordering(Sort.Direction.ASC).build();
    FilterAndOrder filterAndOrder = FilterAndOrder.builder().filter(filter).order(order).build();

    Predicate p =
        PaSpecification.build(null, null, null)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaSpecification.build(filterAndOrder, true, false)
            .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaSpecification.build(filterAndOrder, false, true)
             .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaSpecification.build(filterAndOrder, true, true)
              .toPredicate(root, query, builder);
    assertNotNull(p);

    p =
        PaSpecification.build(filterAndOrder, false, false)
               .toPredicate(root, query, builder);
    assertNotNull(p);
  }
}
