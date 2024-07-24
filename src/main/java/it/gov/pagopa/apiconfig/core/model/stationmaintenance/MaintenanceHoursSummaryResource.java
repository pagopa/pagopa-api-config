package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Model class that hold the hours' summary of stations' maintenance for a specific broker
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceHoursSummaryResource {

    @JsonProperty("used_hours")
    @Schema(description = "Count of used maintenance's hours", required = true)
    @NotNull
    private String usedHours;

    @JsonProperty("scheduled_hours")
    @Schema(description = "Count of scheduled maintenance's hours", required = true)
    @NotNull
    private String scheduledHours;

    @JsonProperty("remaining_hours")
    @Schema(description = "Count of remaining maintenance's hours before annual limit", required = true)
    @NotNull
    private String remainingHours;

    @JsonProperty("extra_hours")
    @Schema(description = "Count of maintenance's hours that exceed annual limit", required = true)
    @NotNull
    private String extraHours;

    @JsonProperty("annual_hours_limit")
    @Schema(description = "Annual limit of maintenance hours", required = true)
    @NotNull
    private String annualHoursLimit;
}