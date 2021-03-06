package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.pagopa.apiconfig.model.PageInfo;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Station CreditorInstitutions
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationCreditorInstitutions {
    @JsonProperty("creditor_institutions")
    @Schema(required = true)
    @NotNull
    @Valid
    private List<StationCreditorInstitution> creditorInstitutionList;

    @JsonProperty("page_info")
    @Schema(required = true)
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
