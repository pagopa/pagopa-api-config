package it.pagopa.pagopa.apiconfig.model.afm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentTypesCosmos {

  private String id;

  private String name;

  private String description;
}
