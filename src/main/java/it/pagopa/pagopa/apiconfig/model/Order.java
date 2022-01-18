package it.pagopa.pagopa.apiconfig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder

public class Order {
    @Nullable
    private String orderBy;

    @NotNull
    private Sort.Direction ordering;

}
