package it.pagopa.pagopa.apiconfig.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
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
@Container(containerName = "serviceamount")
public class ServiceAmountCosmos {

  private Integer paymentAmount;
  private Integer minPaymentAmount;
  private Integer maxPaymentAmount;
}
