package it.pagopa.pagopa.apiconfig.model;

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
    private Filters filters;

    @NotNull
    @Valid
    private Order order;
}
