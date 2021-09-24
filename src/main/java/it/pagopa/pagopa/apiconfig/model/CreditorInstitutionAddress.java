package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Pattern;

/**
 * CreditorInstitutionAddress
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionAddress {
    @JsonProperty("location")
    @Schema(example = "Via delle vie 3")
    private String location;

    @JsonProperty("city")
    @Schema(example = "Lorem")
    private String city;

    @JsonProperty("zip_code")
    @Schema(example = "00187")
    @Pattern(regexp = "^\\d{5}$")
    private String zipCode;

    @JsonProperty("country_code")
    @Schema(example = "RM")
    @Pattern(regexp = "^\\w{2}$")
    private String countryCode;

    @JsonProperty("tax_domicile")
    @Schema()
    private String taxDomicile;


}
