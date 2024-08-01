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
 * Model class that define the input field for creating a station's maintenance
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStationMaintenance {

    @JsonProperty("start_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.DateTimeFormat.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T10:00:00.000+02:00",
            required = true,
            description = "The start date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime startDateTime;

    @JsonProperty("end_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.DateTimeFormat.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
    @Schema(
            example = "2024-04-01T13:00:00.000+02:00",
            required = true,
            description = "The end date time of the station maintenance")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime endDateTime;

    @JsonProperty("stand_in")
    @Schema(description = "StandIn flag", required = true)
    @NotNull
    private Boolean standIn;

    @JsonProperty("station_code")
    @Schema(description = "Code of the station subject of the maintenance", required = true)
    @NotNull
    private String stationCode;
}