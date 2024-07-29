package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.util.Constants;
import it.gov.pagopa.apiconfig.core.util.OffsetDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

/**
 * Model class that define the input field for updating a station's maintenance
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStationMaintenance {

    @JsonProperty("start_date_time")
    @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T10:00:00.000Z",
            description = "The start date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startDateTime;

    @JsonProperty("end_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T13:00:00.000Z",
            required = true,
            description = "The end date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endDateTime;

    @JsonProperty("stand_in")
    @Schema(description = "StandIn flag")
    private Boolean standIn;
}
