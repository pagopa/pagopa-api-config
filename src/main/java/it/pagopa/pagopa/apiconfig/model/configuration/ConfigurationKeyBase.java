package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * ConfigurationKey
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConfigurationKeyBase {

    @JsonProperty("config_value")
    @Schema(example = "180000", required = true)
    @NotNull
    private String configValue;

    @JsonProperty("config_description")
    @Schema(example = " default millisecondi validità token", required = false)
    private String configDescription;

}
