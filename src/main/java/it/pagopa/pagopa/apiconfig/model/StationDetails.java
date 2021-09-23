package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * StationDetails
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationDetails {

    @JsonProperty("station")
    @Schema(required = true)
    @NotNull
    private Station station;

    @JsonProperty("ecs")
    @Schema(required = true)
    @NotNull
    private List<String> ecs = new ArrayList<>();


}
