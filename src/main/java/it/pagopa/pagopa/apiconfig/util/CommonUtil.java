package it.pagopa.pagopa.apiconfig.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtil {

    public static final String YES = "Y";

    /**
     * @param value string value: Y or N
     * @return true if value is equals to Y, false if N
     */
    public static Boolean flagToBoolean(String value){
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
}
