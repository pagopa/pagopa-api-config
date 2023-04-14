package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** CreditorInstitution */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionDetails extends CreditorInstitution {

  @JsonProperty("address")
  @Schema(required = true)
  @Valid
  private CreditorInstitutionAddress address;

  @JsonProperty("psp_payment")
  @Schema(required = true, defaultValue = "true")
  @NotNull
  private Boolean pspPayment;

  @JsonProperty("reporting_ftp")
  @Schema(required = true, defaultValue = "false")
  @NotNull
  private Boolean reportingFtp;

  @JsonProperty("reporting_zip")
  @Schema(required = true, defaultValue = "false")
  @NotNull
  private Boolean reportingZip;
}
