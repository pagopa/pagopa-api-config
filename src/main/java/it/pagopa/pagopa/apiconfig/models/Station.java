package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
public class Station {
    @JsonProperty("id_station")
    @ApiModelProperty(example = "1234567890100", required = true, value = "")
    @NotNull
    @Size(max = 35)
    private String idStation;

    @JsonProperty("enabled")
    @ApiModelProperty(required = true, value = "station enabled")
    @NotNull

    private Boolean enabled = true;

    @JsonProperty("version")
    @ApiModelProperty(required = true, value = "number version")
    @NotNull

    private BigDecimal version;


}
