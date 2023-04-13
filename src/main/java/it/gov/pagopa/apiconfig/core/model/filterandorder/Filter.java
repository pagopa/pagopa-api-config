package it.gov.pagopa.apiconfig.core.model.filterandorder;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Filter {
  @Nullable private String code;

  @Nullable private String name;
}
