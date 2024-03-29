package it.gov.pagopa.apiconfig.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** PaymentType */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentType extends PaymentTypeBase {

  @JsonProperty("payment_type")
  @Schema(example = "AD", required = true)
  @Size(max = 15)
  @Pattern(regexp = "[A-Z]*")
  private String paymentTypeCode;
}
