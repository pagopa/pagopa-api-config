package it.gov.pagopa.apiconfig.core.util;

import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PaSpecification {


    /**
     * This method builds a JPA Specification for the Pa entity based on the provided filter and order criteria
     *
     * @param filterAndOrder the basic filter and order object
     * @param hasCBILL true to filter only PA with CBILL, false to filter only PA without CBILL, null to not filter by CBILL
     * @param hasValidIban true to filter only PA with at least one valid IBAN, false to filter only PA without valid IBAN, null to not filter by IBAN
     * @return the specification
     */
    public static Specification<Pa> build(
            FilterAndOrder filterAndOrder,
            Boolean hasCBILL,
            Boolean hasValidIban) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            baseFilters(filterAndOrder, root, criteriaBuilder, predicates);

            if (hasCBILL != null) {
                if (hasCBILL) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("cbill")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("cbill")));
                }
            }

            if (hasValidIban != null) {
                if (hasValidIban) {
                    predicates.add(criteriaBuilder.isNotEmpty(root.get("ibans")));
                } else {
                    predicates.add(criteriaBuilder.isEmpty(root.get("ibans")));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void baseFilters(FilterAndOrder filterAndOrder, Root<Pa> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (filterAndOrder != null && filterAndOrder.getFilter() != null) {
            String code = filterAndOrder.getFilter().getCode();
            String name = filterAndOrder.getFilter().getName();
            Boolean enabled = filterAndOrder.getFilter().getEnabled();

            if (StringUtils.hasText(code)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("idDominio")), "%" + code.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(name)) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ragioneSociale")), "%" + name.toLowerCase() + "%"));
            }

            if (enabled != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), enabled));
            }
        }
    }
}
