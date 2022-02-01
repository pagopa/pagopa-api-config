package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * ConfigurationKey
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigurationKey extends ConfigurationKeyBase {

    @JsonProperty("config_category")
    @Schema(example = "GLOBAL", required = true)
    @NotBlank
    private String configCategory;

    @JsonProperty("config_key")
    @Schema(example = "default_token_duration_validity_millis", required = true)
    @NotBlank
    private String configKey;

}
