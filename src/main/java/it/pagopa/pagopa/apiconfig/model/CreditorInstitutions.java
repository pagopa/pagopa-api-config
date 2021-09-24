package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutions {
    @JsonProperty("creditor_institutions")
    @Schema(required = true)
    @Valid
    private List<CreditorInstitution> creditorInstitutionList = null;

    @JsonProperty("page_info")
    @Schema(required = true)
    @Valid
    private PageInfo pageInfo;
}
