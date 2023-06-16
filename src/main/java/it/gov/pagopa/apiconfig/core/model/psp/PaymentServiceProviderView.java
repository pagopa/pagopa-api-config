package it.gov.pagopa.apiconfig.core.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentServiceProviderView {

  @JsonProperty("psp_code")
  @Schema(required = true)
  @NotBlank
  @Pattern(regexp = "[A-Z0-9_]{6,14}")
  private String pspCode;

  @JsonProperty("broker_psp_code")
  @Schema(example = "223344556677889900", required = true)
  @NotBlank
  @Size(max = 35)
  private String brokerPspCode;

  @JsonProperty("channel_code")
  @Schema(example = "223344556677889900", required = true)
  @NotBlank
  private String channelCode;

  @JsonProperty("payment_type")
  @Schema(required = true)
  private String paymentType;

  @JsonProperty("payment_method")
  @Schema(required = true)
  private String paymentMethod;
}
