package it.gov.pagopa.apiconfig.core.model.massiveloading;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.Set;

public enum OperationEnum {
    I, D, C, U, M;

    public static final Set<OperationEnum> UPDATE_OP = Set.of(U, M);
    public static final Set<OperationEnum> DELETE_OP = Set.of(D, C);


    public static class OperationEnumConverter extends AbstractBeanField<OperationEnum, String> {

        @Override
        protected OperationEnum convert(String value)
                throws CsvDataTypeMismatchException, CsvConstraintViolationException {

            if (value == null || value.isBlank()) {
                throw new CsvConstraintViolationException(
                        "Column operazione is mandatory. Allowed values: I, D, C, U, M"
                );
            }

            String normalized = value.trim().toUpperCase();
            return switch (normalized) {
                case "I", "D", "C", "U", "M" -> OperationEnum.valueOf(normalized);
                default -> throw new CsvConstraintViolationException(
                        String.format(
                                "Invalid value '%s' for column operazione. Allowed values: I (insert), D/C (delete), U/M (update)",
                                value
                        )
                );
            };
        }
    }
}
