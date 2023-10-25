package it.gov.pagopa.apiconfig.core.model.filterandorder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import org.springframework.lang.Nullable;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Filter {
  @Nullable private String code;
  @Nullable private String name;
  @Nullable private String fiscalCode;
}
