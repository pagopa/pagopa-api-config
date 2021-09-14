package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Station
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
    @JsonProperty("id_station")
    @Schema(example = "1234567890100", required = true, description = "")
    @NotNull
    @Size(max = 35)
    private String idStation;

    @JsonProperty("enabled")
    @Schema(required = true, description = "station enabled", defaultValue = "true")
    @NotNull
    private Boolean enabled = true;

    @JsonProperty("version")
    @Schema(required = true, description = "number version")
    @NotNull
    private BigDecimal version;


}
