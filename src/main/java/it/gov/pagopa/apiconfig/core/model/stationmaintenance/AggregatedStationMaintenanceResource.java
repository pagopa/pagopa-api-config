package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedStationMaintenanceResource {

    @JsonProperty("broker_code")
    @Schema(description = "Code of the broker that owns the stations", required = true)
    @NotNull
    private String brokerCode;

    @JsonProperty("broker_name")
    @Schema(description = "Name of the broker", required = true)
    @NotNull
    private String brokerName;

    @JsonProperty("start_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.DateTimeFormat.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @Schema(example = "2024-04-01T10:00:00.000+02:00", required = true)
    private OffsetDateTime startDateTime;

    @JsonProperty("end_date_time")
    @NotNull
    @JsonFormat(pattern = Constants.DateTimeFormat.ZONED_DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @Schema(example = "2024-04-01T13:00:00.000+02:00", required = true)
    private OffsetDateTime endDateTime;

    @JsonProperty("stations")
    @Schema(description = "Distinct station codes affected by the maintenance", required = true)
    @NotNull
    private List<String> stations;

    @JsonProperty("creditor_institutions")
    @Schema(description = "Distinct creditor institutions linked to the affected stations", required = true)
    @NotNull
    @Valid
    private List<AffectedCreditorInstitutionResource> creditorInstitutions;
}
