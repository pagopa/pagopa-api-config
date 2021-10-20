package it.pagopa.pagopa.apiconfig.util;

import it.pagopa.pagopa.apiconfig.model.PageInfo;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Filter;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.filterandorder.OrderType;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
     * @param calendar to convert
     * @return convert calendar to {@link Timestamp}
     */
    public static Timestamp toTimestamp(XMLGregorianCalendar calendar) {
        return Timestamp.from(calendar.toGregorianCalendar().toZonedDateTime().toInstant());
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
     * Get the field name from enumerations that implements {@link OrderType}.
     * See {@link Order} class.
     * The field name identify the column
     *
     * @param filterAndOrder object with sorting info
     * @return a {@link Sort} object to use with SpringRepository
     */
    public static Sort getSort(FilterAndOrder filterAndOrder) {
        var order = new Sort.Order(filterAndOrder.getOrder().getOrdering(), filterAndOrder.getOrder().getOrderBy().getColumnName());
        return Sort.by(order.ignoreCase());
    }


    /**
     * @param filterByCode filter by code
     * @param filterByName filter by name
     * @param orderBy      order by column
     * @param ordering     direction of ordering
     * @return {@link FilterAndOrder} object
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
     * @param example filter
     * @return a new Example using the custom ExampleMatcher
     */
    public static <T> Example<T> getFilters(T example) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase(true)
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        return Example.of(example, matcher);
    }

    /**
     * @param iban an italian IBAN with length 27
     * @return ABI in the IBAN
     */
    public static String getAbiFromIban(String iban) {
        return iban.substring(5, 10);
    }

    /**
     * @param iban an italian IBAN with length 27
     * @return Conto Corrente in the IBAN
     */
    public static String getCcFromIban(String iban) {
        return iban.substring(15, 27);
    }

    /**
     * @param offsetDateTime to convert
     * @return convert offsetDateTime to {@link Timestamp}
     */
    public static Timestamp toTimestamp(OffsetDateTime offsetDateTime) {
        return Timestamp.from(offsetDateTime.toInstant());
    }
}
