package it.gov.pagopa.apiconfig.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigurationDomain {
  FTP_SERVER("FTP_SERVER"),
  INFORMATIVA_CDI("INFORMATIVA_CDI"),
  INFORMATIVA_PA("INFORMATIVA_PA"),
  PA("PA"),
  PDD("PDD"),
  PSP("PSP"),
  GLOBAL("GLOBAL");

  private final String value;
}
