package it.pagopa.pagopa.apiconfig.model.filterandorder;

import java.util.List;

public interface OrderType {

    String getColumnName();

    List<String> getColumnNames();

    OrderType[] values();
}
