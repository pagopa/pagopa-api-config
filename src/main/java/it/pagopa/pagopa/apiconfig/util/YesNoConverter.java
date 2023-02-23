package it.pagopa.pagopa.apiconfig.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class YesNoConverter implements AttributeConverter<Boolean, String> {

  public static final String YES = "Y";
  public static final String NO = "N";

  @Override
  public String convertToDatabaseColumn(final Boolean value) {
    return Boolean.TRUE.equals(value) ? YES : NO;
  }

  @Override
  public Boolean convertToEntityAttribute(final String value) {
    return value.equals(YES);
  }
}
