package it.gov.pagopa.apiconfig.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import it.gov.pagopa.apiconfig.starter.util.NumericBooleanConverter;

class NumericBooleanConverterTest {

  @Test
  void convertToDatabaseColumn() {
    var converter = new NumericBooleanConverter();
    var result = converter.convertToDatabaseColumn(true);
    assertEquals(1, result);
  }

  @Test
  void convertToEntityAttribute() {
    var converter = new NumericBooleanConverter();
    var result = converter.convertToEntityAttribute(1);
    assertTrue(result);
  }
}
