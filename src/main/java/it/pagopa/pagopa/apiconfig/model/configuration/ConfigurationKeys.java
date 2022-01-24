package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * ConfigurationKeys
 */
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
