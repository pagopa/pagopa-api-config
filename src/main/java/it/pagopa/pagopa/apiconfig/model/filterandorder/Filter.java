package it.pagopa.pagopa.apiconfig.model.filterandorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
@AllArgsConstructor
@Builder
public class Filter {
  @Nullable private String code;

  @Nullable private String name;
}
