package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** CreditorInstitution */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitution {

  @JsonProperty("creditor_institution_code")
  @Schema(example = "1234567890100", required = true)
  @NotBlank
  @Size(max = 35)
  private String creditorInstitutionCode;

  @JsonProperty("enabled")
  @Schema(required = true, description = "creditor institution enabled", defaultValue = "true")
  @NotNull
  private Boolean enabled;

  @JsonProperty("business_name")
  @Schema(example = "Comune di Lorem Ipsum", required = true)
  @NotNull
  @Size(max = 70)
  private String businessName;

  @JsonProperty("description")
  @Schema(
          example = "Descrizione dell'Ente Creditore",
          description = "The description of the Creditor Institution")
  @Size(max = 255)
  private String description;
}
