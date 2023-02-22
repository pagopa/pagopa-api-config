package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelPsp {

  @JsonProperty("psp_code")
  @Schema(required = true)
  @NotBlank
  private String pspCode;

  @JsonProperty("business_name")
  @Schema(required = true)
  @NotNull
  private String businessName;

  @JsonProperty("enabled")
  @Schema(required = true)
  @NotNull
  private Boolean enabled;

  @JsonProperty("payment_types")
  @Schema(required = true)
  @NotNull
  // should be @NotEmpty for requests but it is not compatible for responses
  // check on empty request is managed in the service method
  private List<String> paymentTypeList;
}
