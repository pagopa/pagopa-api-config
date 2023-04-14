package it.gov.pagopa.apiconfig.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CheckItem {

  String title;
  String value;
  Validity valid;
  String note;
  String action;

  @Getter
  @AllArgsConstructor
  public enum Validity {
    VALID("valid"),
    NOT_VALID("not valid");

    private final String value;
  }
}
