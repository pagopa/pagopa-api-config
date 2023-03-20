package it.pagopa.pagopa.apiconfig.cosmos.container;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Container(containerName = "cdis", autoCreateContainer = false)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdiCosmos {

  @Id private String id;
  @PartitionKey private String idPsp;
  private String idCdi;
  private String abi;
  private String cdiStatus;
  private Boolean digitalStamp;
  private String validityDateFrom;
  private List<CdiDetailCosmos> details;
}
