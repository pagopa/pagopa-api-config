package it.gov.pagopa.apiconfig.model.filterandorder;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Sort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Order {

  @NotNull private OrderType orderBy;

  @NotNull private Sort.Direction ordering;

  @Getter
  @AllArgsConstructor
  public enum CreditorInstitution implements OrderType {
    /**
     * To filter by CODE the PA we can use idDominio field of the entity To filter by NAME the PA we
     * can use ragioneSociale field of the entity
     */
    CODE("idDominio"),
    NAME("ragioneSociale");

    /** The field name identify the column */
    private final String columnName;
  }

  @Getter
  @AllArgsConstructor
  public enum Broker implements OrderType {
    CODE("idIntermediarioPa"),
    NAME("codiceIntermediario");

    private final String columnName;
  }

  @Getter
  @AllArgsConstructor
  public enum Station implements OrderType {
    CODE("idStazione");

    private final String columnName;
  }

  @Getter
  @AllArgsConstructor
  public enum Psp implements OrderType {
    CODE("idPsp"),
    NAME("descrizione");

    private final String columnName;
  }

  @Getter
  @AllArgsConstructor
  public enum BrokerPsp implements OrderType {
    CODE("idIntermediarioPsp"),
    NAME("codiceIntermediario");

    private final String columnName;
  }

  @Getter
  @AllArgsConstructor
  public enum Channel implements OrderType {
    CODE("idCanale");

    private final String columnName;
  }
}
