package it.pagopa.pagopa.apiconfig.util;

import it.pagopa.pagopa.apiconfig.model.PageInfo;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class CommonUtil {

    public static final String YES = "Y";

    /**
     * @param value string value: Y or N
     * @return true if value is equals to Y, false if N
     */
    public static Boolean flagToBoolean(String value) {
        return YES.equals(value);
    }

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
}
