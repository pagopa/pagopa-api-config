package it.gov.pagopa.apiconfig.core.model.filterandorder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@NotNull
public class FilterAndOrder {
  @NotNull @Valid private Filter filter;

  @NotNull @Valid private Order order;
}
