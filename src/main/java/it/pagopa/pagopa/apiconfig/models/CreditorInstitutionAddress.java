package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
public class CreditorInstitutionAddress {
    @JsonProperty("location")
    @ApiModelProperty(example = "Via delle vie 3", value = "")


    private String location;

    @JsonProperty("city")
    @ApiModelProperty(example = "Lorem", value = "")


    private String city;

    @JsonProperty("zipCode")
    @ApiModelProperty(example = "00187", value = "")

    @Pattern(regexp = "^\\d{5}$")
    private String zipCode;

    @JsonProperty("country_code")
    @ApiModelProperty(example = "RM", value = "")

    @Pattern(regexp = "^\\w{2}$")
    private String countryCode;


}
