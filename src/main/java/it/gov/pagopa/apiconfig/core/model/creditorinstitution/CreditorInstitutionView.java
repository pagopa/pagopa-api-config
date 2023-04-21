package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionView {

  @JsonProperty("creditor_institution_code")
  @Schema(example = "1234567890100", required = true)
  @NotBlank
  @Size(max = 35)
  private String idDominio;
  
  @JsonProperty("broker_code")
  @Schema(example = "223344556677889900", required = true)
  @NotBlank
  @Size(max = 35)
  private String idIntermediarioPa;
  
  @JsonProperty("station_code")
  @Schema(example = "1234567890100", required = true)
  @NotBlank
  @Size(max = 35)
  private String idStazione;
  
  @JsonProperty("aux_digit")
  private Long auxDigit;
  
  @Min(0)
  @JsonProperty("application_code")
  private Long progressivo;
  
  @JsonProperty("segregation_code")
  private Long segregazione;

  @JsonProperty("mod4")
  private Boolean quartoModello;

}
