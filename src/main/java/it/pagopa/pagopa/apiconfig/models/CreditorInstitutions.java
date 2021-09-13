package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import java.util.List;

/**
 * CreditorInstitutions
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditorInstitutions {
    @JsonProperty("creditor_institutions")
    @ApiModelProperty(value = "")
    @Valid
    private List<CreditorInstitution> creditorInstitutionList = null;


}
