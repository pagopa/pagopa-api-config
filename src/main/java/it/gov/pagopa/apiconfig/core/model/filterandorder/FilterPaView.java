package it.gov.pagopa.apiconfig.core.model.filterandorder;

import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FilterPaView {
  @Nullable private String creditorInstitutionCode; 
  @Nullable private String paBrokerCode; 
  @Nullable private String stationCode; 
  @Nullable private Long auxDigit; 
  @Nullable private Long applicationCode; 
  @Nullable private Long segregationCode; 
  @Nullable private Boolean mod4;
}