package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * PaymentType
 */
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
    private String paymentTypeCode;

}
