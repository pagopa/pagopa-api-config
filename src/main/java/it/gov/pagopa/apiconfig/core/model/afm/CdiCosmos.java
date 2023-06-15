package it.gov.pagopa.apiconfig.core.model.afm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdiCosmos {
  private String id;
  private String idPsp;
  private String idCdi;
  private String abi;
  private String cdiStatus;
  private Boolean digitalStamp;
  private String validityDateFrom; // LocalDate
  private List<CdiDetailCosmos> details;
}
