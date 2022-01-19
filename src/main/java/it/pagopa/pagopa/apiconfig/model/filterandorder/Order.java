package it.pagopa.pagopa.apiconfig.model.filterandorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@Builder
public class Order {

    @NotNull
    private OrderType orderBy;

    @NotNull
    private Sort.Direction ordering;


    @Getter
    @AllArgsConstructor
    public enum CreditorInstitution implements OrderType {
        CODE("idDominio"),
        NAME("ragioneSociale");

        private final String columnName;

        public List<String> getColumnNames() {
            return Arrays.stream(values())
                    .map(OrderType::getColumnName)
                    .collect(Collectors.toList());
        }

    }

    @Getter
    @AllArgsConstructor
    public enum Broker implements OrderType {
        CODE("idIntermediarioPa"),
        NAME("codiceIntermediario");

        private final String columnName;

        public List<String> getColumnNames() {
            return Arrays.stream(values())
                    .map(OrderType::getColumnName)
                    .collect(Collectors.toList());
        }
    }
}
