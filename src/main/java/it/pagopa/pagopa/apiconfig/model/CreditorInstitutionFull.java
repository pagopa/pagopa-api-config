package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

/**
 * CreditorInstitution
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionFull extends CreditorInstitutionLight{

    @JsonProperty("address")
    @Schema(required = true)
    @NotNull
    private CreditorInstitutionAddress address;

    @JsonProperty("psp_payment")
    @Schema(required = true, defaultValue = "true")
    @NotNull
    private Boolean pspPayment = true;

    @JsonProperty("reporting_ftp")
    @Schema(required = true, defaultValue = "false")
    @NotNull
    private Boolean reportingFtp = false;

    @JsonProperty("reporting_zip")
    @Schema(required = true, defaultValue = "false")
    @NotNull
    private Boolean reportingZip = false;

    @JsonProperty("payment_cancellation")
    @Schema(required = true, defaultValue = "false")
    @NotNull
    private Boolean paymentCancellation = false;


}
