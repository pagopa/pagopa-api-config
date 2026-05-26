package it.gov.pagopa.apiconfig.core.model.massiveloading;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static it.gov.pagopa.apiconfig.core.util.Constants.DateTimeFormat.DATE_FORMAT_PATTERN;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IbanMassLoadCsv {

    @CsvBindByName(required = true, column = "iddominio")
    private String creditorInstitutionCode;

    @CsvBindByName(column = "descrizione")
    private String description;

    @CsvBindByName(required = true, column = "iban")
    private String iban;

    @CsvCustomBindByName(column = "dataattivazioneiban", converter = NullableLocalDateConverter.class)
    private LocalDate activationDate;

    @CsvCustomBindByName(column = "datascadenzaiban", converter = NullableLocalDateConverter.class)
    private LocalDate dueDate;

    @CsvCustomBindByName(column = "operazione", required = true, converter = OperationEnum.OperationEnumConverter.class)
    private OperationEnum operation;


    public static class NullableLocalDateConverter extends AbstractBeanField<LocalDate, String> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);

        @Override
        protected LocalDate convert(String value)
                throws CsvDataTypeMismatchException, CsvConstraintViolationException {

            if (value == null || value.isBlank()) {
                return null;
            }

            try {
                return LocalDate.parse(value.trim(), FORMATTER);
            } catch (DateTimeParseException e) {
                throw new CsvConstraintViolationException(
                        String.format("Column %s must have format yyyy-MM-dd (received: '%s')", getField().getName(), value)
                );
            }
        }
    }
}
