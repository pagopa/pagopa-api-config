package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
public class CreditorIstitutionDetails {
    @JsonProperty("creditor_istitution")
    @ApiModelProperty(value = "")


    private CreditorIstitution creditorIstitution;

    @JsonProperty("stations")
    @ApiModelProperty(required = true, value = "")
    @NotNull
    @Valid
    private List<Station> stations = new ArrayList<>();


}
