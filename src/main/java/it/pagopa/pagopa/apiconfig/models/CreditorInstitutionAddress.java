package it.pagopa.pagopa.apiconfig.models;

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
    @Schema(example = "Via delle vie 3", description = "")
    private String location;

    @JsonProperty("city")
    @Schema(example = "Lorem", description = "")
    private String city;

    @JsonProperty("zipCode")
    @Schema(example = "00187", description = "")

    @Pattern(regexp = "^\\d{5}$")
    private String zipCode;

    @JsonProperty("country_code")
    @Schema(example = "RM", description = "")

    @Pattern(regexp = "^\\w{2}$")
    private String countryCode;


}
