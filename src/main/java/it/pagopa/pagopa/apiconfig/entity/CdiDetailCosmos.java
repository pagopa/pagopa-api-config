package it.pagopa.pagopa.apiconfig.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Container(containerName = "cdidetail")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdiDetailCosmos {
    private String idBrokerPsp;
    private String idChannel;
    private String name;
    private String description;
    private Long paymentType;
    private Boolean onUs;
    private Boolean channelApp;
    private Boolean channelCardsCart;
    private List<ServiceAmountCosmos> serviceAmount;
}
