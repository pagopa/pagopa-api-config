package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionStationEdit {

  @JsonProperty("station_code")
  @Schema(example = "1234567890100", required = true)
  @NotEmpty
  @Size(max = 35)
  private String stationCode;

  @JsonProperty("aux_digit")
  @Schema(
      example = "1",
      allowableValues = {"0", "1", "2", "3"})
  @Min(0)
  @Max(3)
  private Long auxDigit;

  @Min(0)
  @JsonProperty("application_code")
  private Long applicationCode;

  @Min(0)
  @JsonProperty("segregation_code")
  private Long segregationCode;

  @JsonProperty("mod4")
  private Boolean mod4;

  @JsonProperty("broadcast")
  private Boolean broadcast;

  @Nullable
  @JsonProperty("aca")
  private Boolean aca;

  @Nullable
  @JsonProperty("stand_in")
  private Boolean standIn;

  @JsonIgnore private Pa fkPa;

  @JsonIgnore private Stazioni fkStazioni;
}
