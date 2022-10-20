package it.pagopa.pagopa.apiconfig.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLDateAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final String pattern = "yyyy-MM-dd'T'HH:mm:ss";

    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {

        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(pattern);
        }
    };

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return dateFormat.get().format(v);
    }

    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return LocalDateTime.parse(v, DateTimeFormatter.ofPattern(pattern));
    }

}
