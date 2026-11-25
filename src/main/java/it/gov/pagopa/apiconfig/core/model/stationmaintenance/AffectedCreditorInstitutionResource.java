package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffectedCreditorInstitutionResource {

    @JsonProperty("business_name")
    @Schema(description = "Creditor institution's business name", required = true)
    @NotNull
    private String businessName;

    @JsonProperty("creditor_institution_code")
    @Schema(description = "Creditor institution's code", required = true)
    @NotNull
    private String creditorInstitutionCode;
}
