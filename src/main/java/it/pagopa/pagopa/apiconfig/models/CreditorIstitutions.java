package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CreditorIstitutions
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditorIstitutions {
    @JsonProperty("creditor_istitutions")
    @ApiModelProperty(value = "")
    @Valid
    private List<CreditorIstitution> creditorIstitutionList = null;


}
