package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * CreditorInstitutionDetails
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionDetails {
    @JsonProperty("creditor_institution")
    @Schema(description = "")
    private CreditorInstitution creditorInstitution;

    @JsonProperty("stations")
    @Schema(required = true, description = "")
    @NotNull
    @Valid
    private List<Station> stations = new ArrayList<>();


}
