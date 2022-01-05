package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentServiceProvider {

    @JsonProperty("psp_code")
    @Schema(required = true)
    @NotNull
    private String pspCode;

    @JsonProperty("enabled")
    @Schema(required = true)
    private Boolean enabled;

    @JsonProperty("business_name")
    @Schema(required = true)
    @NotNull
    private String businessName;

}
