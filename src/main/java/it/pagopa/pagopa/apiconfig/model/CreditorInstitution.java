package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * CreditorInstitution
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitution {
    @JsonProperty("id_domain")
    @Schema(example = "1234567890100", required = true)
    @NotNull
    @Size(max = 35)
    private String idDomain;

    @JsonProperty("enabled")
    @Schema(required = true, description = "creditor institution enabled", defaultValue = "true")
    @NotNull
    private Boolean enabled = true;

    @JsonProperty("business_name")
    @Schema(example = "Comune di Lorem Ipsum", required = true)
    @NotNull
    @Size(max = 70)
    private String businessName;

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
