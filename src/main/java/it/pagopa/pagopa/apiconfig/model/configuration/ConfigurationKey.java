package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * ConfigurationKey
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigurationKey {

    @JsonProperty("config_category")
    @Schema(example = "GLOBAL", required = true)
    @NotBlank
    private String configCategory;

    @JsonProperty("config_key")
    @Schema(example = "default_token_duration_validity_millis", required = true)
    @NotBlank
    private String configKey;

    @JsonProperty("config_value")
    @Schema(example = "180000", required = true)
    @NotBlank
    private String configValue;

    @JsonProperty("config_description")
    @Schema(example = " default millisecondi validità token", required = false)
    private String configDescription;

}
