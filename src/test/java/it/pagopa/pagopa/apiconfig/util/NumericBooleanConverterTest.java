package it.pagopa.pagopa.apiconfig.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
