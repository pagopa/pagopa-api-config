package it.gov.pagopa.apiconfig.core.model.configuration;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** ConfigurationKeys */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigurationKeys {
  @JsonProperty("configuration_keys")
  @Schema(required = true)
  @NotNull
  @Valid
  private List<ConfigurationKey> configurationKeyList;
}
