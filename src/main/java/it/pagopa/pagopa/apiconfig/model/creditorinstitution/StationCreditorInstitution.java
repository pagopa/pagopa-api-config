package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Stations
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude() // this is necessary to override CreditorInstitution config
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationCreditorInstitution extends CreditorInstitution {

    @JsonProperty("application_code")
    private Long applicationCode;

    @JsonProperty("aux_digit")
    private Long auxDigit;

    @JsonProperty("segregation_code")
    private Long segregationCode;

    @JsonProperty("mod4")
    private Boolean mod4;

    @JsonProperty("broadcast")
    private Boolean broadcast;

}
