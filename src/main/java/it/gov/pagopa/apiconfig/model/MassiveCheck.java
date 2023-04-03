package it.gov.pagopa.apiconfig.model;

import java.util.List;

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
public class MassiveCheck {

  private String fileName;

  private List<CheckItem> checkItems;
}
