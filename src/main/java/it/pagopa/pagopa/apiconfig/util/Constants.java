package it.pagopa.pagopa.apiconfig.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

    @UtilityClass
    public static class DateTimeFormat {
        public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_WARNING = "X-Warning";

}
