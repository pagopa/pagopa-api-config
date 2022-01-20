package it.pagopa.pagopa.apiconfig.util;

import it.pagopa.pagopa.apiconfig.model.PageInfo;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Filter;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.filterandorder.OrderType;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Optional;

@UtilityClass
public class CommonUtil {

    /**
     * @param zipcode zip code in long
     * @return zip code in string with the format %05d (add leading zeroes to number)
     * Example 00123
     */
    public static String numberToZipCode(Long zipcode) {
        return zipcode == null ? null : String.format("%05d", zipcode);
    }


    /**
     * @param page Page returned from the database
     * @return return the page info
     */
    public <T> PageInfo buildPageInfo(Page<T> page) {
        return PageInfo.builder()
                .page(page.getNumber())
                .limit(page.getSize())
                .totalPages(page.getTotalPages())
                .itemsFound(page.getNumberOfElements())
                .build();
    }

    /**
     * @param timestamp {@link Timestamp} to convert
     * @return convert timestamp to {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp != null ? OffsetDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC) : null;
    }


    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    /**
     * @param value value to deNullify.
     * @return return false if value is null
     */
    public static Boolean deNull(Boolean value) {
        return Optional.ofNullable(value).orElse(false);
    }


    /**
     * @param filterAndOrder object with sorting info
     * @return a {@link Sort} object to use with SpringRepository
     */
    public static Sort getSort(FilterAndOrder filterAndOrder) {
        return Sort.by(filterAndOrder.getOrder().getOrdering(), filterAndOrder.getOrder().getOrderBy().getColumnName());
    }


    /**
     * @param filterByCode
     * @param filterByName
     * @param orderBy
     * @param ordering
     * @return
     */
    public static FilterAndOrder getFilterAndOrder(String filterByCode, String filterByName, OrderType orderBy, Sort.Direction ordering) {
        return FilterAndOrder.builder()
                .filter(Filter.builder()
                        .code(filterByCode)
                        .name(filterByName)
                        .build())
                .order(Order.builder()
                        .orderBy(orderBy)
                        .ordering(ordering)
                        .build())
                .build();
    }

    /**
     * @param filterAndOrder
     * @param clazz
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> Example<T> getFilters(FilterAndOrder filterAndOrder, Class<T> clazz) {
        OrderType orderType = filterAndOrder.getOrder().getOrderBy();

        T example = clazz.getDeclaredConstructor().newInstance();

        for (OrderType enumeration : orderType.getValues()) {
            String value = (String) filterAndOrder.getFilter().getClass().getMethod("get" + StringUtils.capitalize(enumeration.getName().toLowerCase(Locale.ROOT)))
                    .invoke(filterAndOrder.getFilter());
            example.getClass().getMethod("set" + StringUtils.capitalize(enumeration.getColumnName()), String.class)
                    .invoke(example, value);
        }
//        example.getClass().getMethod("set" + StringUtils.capitalize(orderType.getCode()), String.class)
//                .invoke(example, filterAndOrder.getFilter().getCode());
//
//        if (orderType.getName() != null) {
//            example.getClass().getMethod("set" + StringUtils.capitalize(orderType.getName()), String.class)
//                    .invoke(example, filterAndOrder.getFilter().getName());
//        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        return Example.of(example, matcher);
    }
}
