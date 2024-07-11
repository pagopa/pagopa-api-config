package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

/** Stations */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionStation extends Station {
  @Min(0)
  @JsonProperty("application_code")
  private Long applicationCode;

  @JsonProperty("aux_digit")
  private Long auxDigit;

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
}
