package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionStationEdit {

    @JsonProperty("station_code")
    @Schema(example = "1234567890100", required = true)
    @NotEmpty
    @Size(max = 35)
    private String stationCode;

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

    @JsonIgnore
    private Pa fkPa;

    @JsonIgnore
    private Stazioni fkStazioni;
}
