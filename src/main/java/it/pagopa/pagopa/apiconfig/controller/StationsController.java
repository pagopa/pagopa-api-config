package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import it.pagopa.pagopa.apiconfig.model.Stations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/stations")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class StationsController {

    /**
     * GET /stations : Get paginated list of stations
     *
     * @param limit                   Number of elements on one page. Default = 50
     * @param page                    Page number. Page value starts from 0
     * @param intermediaryCode        Filter by intermediary
     * @param creditorInstitutionCode Filter by creditor institution
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get paginated list of stations", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Stations.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    public ResponseEntity<Stations> getStations(
            @Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page,
            @Parameter(description = "Filter by intermediary") @RequestParam(name = "intermediarycode", required = false) String intermediaryCode,
            @Parameter(description = "Filter by creditor institution") @RequestParam(name = "creditorinstitutioncode", required = false) String creditorInstitutionCode) {
        return ResponseEntity.ok(Stations.builder().build());
    }


    /**
     * GET /stations/{stationcode} : Get station details
     *
     * @param stationCode station code. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get station details", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StationDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{stationcode}",
            produces = {"application/json"}
    )
    public ResponseEntity<StationDetails> getStation(@Size(max = 50) @Parameter(description = "station code.", required = true) @PathVariable("stationcode") String stationCode) {
        return ResponseEntity.ok(StationDetails.builder().build());
    }



}
