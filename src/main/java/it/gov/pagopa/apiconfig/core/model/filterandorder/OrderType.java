package it.gov.pagopa.apiconfig.core.model.filterandorder;

/** This interface is implemented by the enumerations in the {@link Order} class */
public interface OrderType {

  /**
   * @return the name of the field of the entity that identifies the column in the DB
   */
  String getColumnName();
}
