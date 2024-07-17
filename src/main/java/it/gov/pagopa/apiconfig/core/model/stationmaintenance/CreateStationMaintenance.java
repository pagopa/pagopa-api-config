package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Model class that define the input field for creating a station's maintenance
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStationMaintenance {

    @JsonProperty("start_date_time")
    @Schema(required = true)
    @NotNull
    private OffsetDateTime startDateTime;

    @JsonProperty("end_date_time")
    @Schema(required = true)
    @NotNull
    private OffsetDateTime endDateTime;

    @JsonProperty("stand_in")
    @Schema(required = true)
    @NotNull
    private Boolean standIn;

    @JsonProperty("station_code")
    @Schema(required = true)
    @NotNull
    private String stationCode;
}