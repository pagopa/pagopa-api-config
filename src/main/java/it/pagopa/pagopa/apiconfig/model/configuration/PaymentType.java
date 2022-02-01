package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * PaymentType
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentType {

    @JsonProperty("description")
    @Schema(example = "Addebito diretto")
    @Size(max = 35)
    private String description;

    @JsonProperty("payment_type")
    @Schema(example = "AD", required = true)
    @Size(max = 15)
    private String paymentType;

}
