package it.gov.pagopa.apiconfig.core.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * This class permits to deserialize a date, encapsulated in {@code OffsetDateTime} object, from a
 * JSON body request using Jackson deserializer.
 */
public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

  private final DateTimeFormatter dateFormatter =
      new DateTimeFormatterBuilder()
          .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
          .optionalStart()
          .appendOffset("+HH:MM", "+00:00")
          .optionalEnd() // for '+00:00' offset
          .optionalStart()
          .appendOffset("+HHMM", "+0000")
          .optionalEnd() // for '+0000' offset
          .optionalStart()
          .appendOffset("+HH", "+00")
          .optionalEnd() // for '+00' offset
          .optionalStart()
          .appendPattern("X")
          .optionalEnd() // for 'Z' offset
          .toFormatter();

  @Override
  public OffsetDateTime deserialize(JsonParser parser, DeserializationContext context)
      throws IOException {
    return OffsetDateTime.parse(parser.getText(), dateFormatter);
  }
}
