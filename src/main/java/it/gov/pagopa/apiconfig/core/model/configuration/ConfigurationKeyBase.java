package it.gov.pagopa.apiconfig.core.model.configuration;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** ConfigurationKey */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigurationKeyBase {

  @JsonProperty("config_value")
  @Schema(example = "180000", required = true)
  @NotBlank
  private String configValue;

  @JsonProperty("config_description")
  @Schema(example = " default millisecondi validità token", required = false)
  private String configDescription;
}
