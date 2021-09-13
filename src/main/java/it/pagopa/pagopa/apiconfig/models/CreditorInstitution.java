package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
public class CreditorInstitution {
    @JsonProperty("id_dominio")
    @ApiModelProperty(example = "1234567890100", required = true, value = "")
    @NotNull
    @Size(max = 35)
    private String idDominio;

    @JsonProperty("enabled")
    @ApiModelProperty(required = true, value = "creditor institution enabled")
    @NotNull

    private Boolean enabled = true;

    @JsonProperty("business_name")
    @ApiModelProperty(example = "Comune di Lorem Ipsum", required = true, value = "")
    @NotNull
    @Size(max = 70)
    private String businessName;

    @JsonProperty("address")
    @ApiModelProperty(required = true, value = "")
    @NotNull

    private CreditorInstitutionAddress address;

    @JsonProperty("psp_payment")
    @ApiModelProperty(required = true, value = "")
    @NotNull

    private Boolean pspPayment = true;

    @JsonProperty("reporting_ftp")
    @ApiModelProperty(required = true, value = "")
    @NotNull

    private Boolean reportingFtp = false;

    @JsonProperty("reporting_zip")
    @ApiModelProperty(required = true, value = "")
    @NotNull

    private Boolean reportingZip = false;

    @JsonProperty("payment_cancellation")
    @ApiModelProperty(required = true, value = "")
    @NotNull

    private Boolean paymentCancellation = false;


}
