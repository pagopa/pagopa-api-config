package it.pagopa.pagopa.apiconfig.model.filterandorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;

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

        @Override
        public String getCode() {
            return CODE.getColumnName();
        }

        @Override
        public String getName() {
            return NAME.getColumnName();
        }

    }

    @Getter
    @AllArgsConstructor
    public enum Broker implements OrderType {
        CODE("idIntermediarioPa"),
        NAME("codiceIntermediario");

        private final String columnName;

        @Override
        public String getCode() {
            return CODE.getColumnName();
        }

        @Override
        public String getName() {
            return NAME.getColumnName();
        }
    }

    @Getter
    @AllArgsConstructor
    public enum Station implements OrderType {
        CODE("idStazione");

        private final String columnName;

        @Override
        public String getCode() {
            return CODE.getColumnName();
        }

        @Override
        public String getName() {
            return null;
        }
    }
}
