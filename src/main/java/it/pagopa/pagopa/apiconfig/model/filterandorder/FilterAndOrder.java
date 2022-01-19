package it.pagopa.pagopa.apiconfig.model.filterandorder;

import lombok.Builder;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NotNull
public class FilterAndOrder {
    @NotNull
    @Valid
    private Filter filter;

    @NotNull
    @Valid
    private Order order;
}
