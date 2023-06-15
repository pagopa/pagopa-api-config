package it.gov.pagopa.apiconfig.core.model.afm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.gov.pagopa.apiconfig.starter.entity.ServiceAmountCosmos;
import java.util.List;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdiDetailCosmos {
  private String idBrokerPsp;
  private String idChannel;
  private String name;
  private String description;
  private String paymentType;
  private Boolean channelApp;
  private Boolean channelCardsCart;
  private List<ServiceAmountCosmos> serviceAmount;
}
