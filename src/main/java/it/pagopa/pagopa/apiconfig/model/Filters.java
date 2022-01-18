package it.pagopa.pagopa.apiconfig.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class Filters {
    @NotNull
    private String code;

    @Nullable
    private String name;
}
