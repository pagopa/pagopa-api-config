package it.pagopa.pagopa.apiconfig.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class XMLDateAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public String marshal(LocalDateTime v) {
        return v.format(DateTimeFormatter.ofPattern(PATTERN));
    }

    @Override
    public LocalDateTime unmarshal(String v) {
        return LocalDateTime.parse(v, DateTimeFormatter.ofPattern(PATTERN));
    }

}
