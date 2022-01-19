package it.pagopa.pagopa.apiconfig.model.filterandorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@Builder
public class Filter {
    @NotNull
    private String code;

    @Nullable
    private String name;
}
