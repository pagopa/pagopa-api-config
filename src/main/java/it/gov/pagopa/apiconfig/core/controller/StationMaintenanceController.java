package it.gov.pagopa.apiconfig.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.apiconfig.core.model.ProblemJson;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceListResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.UpdateStationMaintenance;
import it.gov.pagopa.apiconfig.core.service.StationMaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@RestController
@RequestMapping(path = "/brokers")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
@Validated
public class StationMaintenanceController {

    private final StationMaintenanceService stationMaintenanceService;

    @Autowired
    public StationMaintenanceController(StationMaintenanceService stationMaintenanceService) {
        this.stationMaintenanceService = stationMaintenanceService;
    }

    @Operation(summary = "Get a paginated list of station's maintenance for the specified broker",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceListResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/{brokercode}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<StationMaintenanceListResource> getStationMaintenances(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Station's code") @RequestParam(required = false) String stationCode,
            @Parameter(description = "Start date of maintenance, used to retrieve all maintenance that start before the provided date (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')", example = "2024-04-01T10:00:00.000Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTimeBefore,
            @Parameter(description = "Start date of maintenance, used to retrieve all maintenance that start after the provided date (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')", example = "2024-04-01T10:00:00.000Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDateTimeAfter,
            @Parameter(description = "End date of maintenance, used to retrieve all maintenance that start before the provided date (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')", example = "2024-04-01T13:00:00.000Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTimeBefore,
            @Parameter(description = "End date of maintenance, used to retrieve all maintenance that start after the provided date (yyyy-MM-dd'T'HH:mm:ss.SSS'Z')", example = "2024-04-01T13:00:00.000Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDateTimeAfter,
            @Parameter(description = "Number of items for page") @RequestParam(required = false, defaultValue = "50") @Positive Integer limit,
            @Parameter(description = "Page number") @RequestParam(required = false, defaultValue = "0") @Min(0) @PositiveOrZero Integer page
    ) {
        return ResponseEntity.ok(
                this.stationMaintenanceService.getStationMaintenances(
                        brokerCode,
                        stationCode,
                        startDateTimeBefore,
                        startDateTimeAfter,
                        endDateTimeBefore,
                        endDateTimeAfter,
                        PageRequest.of(page, limit)
                ));
    }

    @Operation(summary = "Create a maintenance for the specified station",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @PostMapping(value = "/{brokercode}/station-maintenances", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<StationMaintenanceResource> createStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @RequestBody @Valid @NotNull CreateStationMaintenance createStationMaintenance
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.stationMaintenanceService.createStationMaintenance(brokerCode, createStationMaintenance));
    }

    @Operation(summary = "Update a maintenance for the specified station",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @PutMapping(value = "/{brokercode}/station-maintenances/{maintenanceid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<StationMaintenanceResource> updateStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenanceid") Long maintenanceId,
            @RequestBody @Valid @NotNull UpdateStationMaintenance updateStationMaintenance
    ) {
        return ResponseEntity.ok(
                this.stationMaintenanceService.
                        updateStationMaintenance(brokerCode, maintenanceId, updateStationMaintenance)
        );
    }

    @Operation(summary = "Get a maintenance for the specified station, given its broker code and maintenance id",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Created",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = StationMaintenanceResource.class))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
                    @ApiResponse(responseCode = "500", description = "Service unavailable",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
            })
    @GetMapping(value = "/{brokercode}/station-maintenances/{maintenanceid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<StationMaintenanceResource> getStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenanceid") Long maintenanceId
    ) {
        return ResponseEntity.ok(this.stationMaintenanceService.getStationMaintenance(brokerCode, maintenanceId));
    }


    @Operation(summary = "Get the hours' summary of stations' maintenance for the specified broker",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = MaintenanceHoursSummaryResource.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @GetMapping(value = "/{brokercode}/station-maintenances/summary", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MaintenanceHoursSummaryResource> getBrokerMaintenancesSummary(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Year of maintenance (yyyy)", example = "2024") @RequestParam @Size(min = 4, max = 4) String maintenanceYear
    ) {
        return ResponseEntity.ok(this.stationMaintenanceService.getBrokerMaintenancesSummary(brokerCode, maintenanceYear));
    }

    @Operation(summary = "Delete a station's maintenance",
            security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))
    })
    @DeleteMapping(value = "/{brokercode}/station-maintenances/{maintenanceid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteStationMaintenance(
            @Parameter(description = "Broker's tax code") @PathVariable("brokercode") String brokerCode,
            @Parameter(description = "Maintenance's id") @PathVariable("maintenanceid") Long maintenanceId
    ) {
        this.stationMaintenanceService.deleteStationMaintenance(brokerCode, maintenanceId);
        return ResponseEntity.ok().build();
    }
}
