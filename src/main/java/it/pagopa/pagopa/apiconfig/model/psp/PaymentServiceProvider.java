package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
public class PaymentServiceProvider {

  @JsonProperty("psp_code")
  @Schema(required = true)
  @NotBlank
  @Pattern(regexp = "[A-Z0-9_]{6,14}")
  private String pspCode;

  @JsonProperty("enabled")
  @Schema(required = true)
  @NotNull
  private Boolean enabled;

  @JsonProperty("business_name")
  @Schema(required = true)
  @NotNull
  private String businessName;
}
