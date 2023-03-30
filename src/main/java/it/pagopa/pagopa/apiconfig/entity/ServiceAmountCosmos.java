package it.pagopa.pagopa.apiconfig.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class ServiceAmountCosmos {
  private Integer paymentAmount;
  private Integer minPaymentAmount;
  private Integer maxPaymentAmount;
}
