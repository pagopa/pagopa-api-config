package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Valid
public class PaymentServiceProviderDetails extends PaymentServiceProvider {

    @JsonProperty("abi")
    private String abi;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("transfer")
    private Boolean transfer;

    @JsonProperty("my_bank_code")
    @Schema(description = "MyBank code")
    private String myBankCode;

    @JsonProperty("stamp")
    private Boolean stamp;

    @JsonProperty("agid_psp")
    @Schema(description = "True if the PSP is internal")
    private Boolean agidPsp = false;

    @JsonProperty("tax_code")
    private String taxCode;

    @JsonProperty("vat_number")
    private String vatNumber;


}
