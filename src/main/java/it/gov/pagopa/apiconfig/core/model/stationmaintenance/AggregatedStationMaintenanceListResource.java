package it.gov.pagopa.apiconfig.core.model.stationmaintenance;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedStationMaintenanceListResource {

    @JsonProperty("station_maintenance_list")
    @Schema(description = "List of aggregated station maintenances", required = true)
    @NotNull
    @Valid
    private List<AggregatedStationMaintenanceResource> maintenanceList;

    @JsonProperty("page_info")
    @Schema(required = true)
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
