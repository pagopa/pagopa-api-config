package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Ibans;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.CreditorInstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class CreditorInstitutionsController {

    @Autowired
    private CreditorInstitutionsService creditorInstitutionsService;


    /**
     * GET /creditorinstitutions : Get paginated list of creditor institutions
     *
     * @param limit Number of elements on one page. Default = 50
     * @param page  Page number. Page value starts from 0
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get paginated list of creditor institutions", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutions.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutions> getCreditorInstitutions(@Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit, @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page) {
        return ResponseEntity.ok(creditorInstitutionsService.getCreditorInstitutions(limit, page));
    }

    /**
     * GET /creditorinstitutions/{creditorinstitutioncode} : Get creditor institution details
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get creditor institution details", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/{creditorinstitutioncode}", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionDetails> getCreditorInstitution(@Size(min = 1, max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode) {
        return ResponseEntity.ok(creditorInstitutionsService.getCreditorInstitution(creditorInstitutionCode));
    }

    @Operation(summary = "Create creditor institution", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @PostMapping(value = "", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionDetails> createCreditorInstitution(@RequestBody @Valid @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creditorInstitutionsService.createCreditorInstitution(creditorInstitutionDetails));
    }

    @Operation(summary = "Update creditor institution", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @PutMapping(value = "/{creditorinstitutioncode}", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionDetails> updateCreditorInstitution(@Size(min = 1, max = 50) @Parameter(description = "The fiscal code of the Organization to update", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                                @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The values to update of the organization", required = true) @RequestBody @Valid @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        return ResponseEntity.ok(creditorInstitutionsService.updateCreditorInstitution(creditorInstitutionCode, creditorInstitutionDetails));
    }

    @Operation(summary = "Delete creditor institution", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @DeleteMapping(value = "/{creditorinstitutioncode}", produces = {"application/json"})
    public ResponseEntity<Void> deleteCreditorInstitution(@Size(min = 1, max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode) {
        creditorInstitutionsService.deleteCreditorInstitution(creditorInstitutionCode);
        return ResponseEntity.ok().build();
    }


    /**
     * GET /creditorinstitutions/{creditorinstitutioncode}/stations : Get station details and relation info with creditor institution
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get station details and relation info with creditor institution", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionStationList.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{creditorinstitutioncode}/stations",
            produces = {"application/json"}
    )
    public ResponseEntity<CreditorInstitutionStationList> getCreditorInstitutionStations(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode) {
        return ResponseEntity.ok(creditorInstitutionsService.getCreditorInstitutionStations(creditorInstitutionCode));

    }

    @Operation(summary = "Create station details and relation info with creditor institution", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionStationEdit.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @PostMapping(
            value = "/{creditorinstitutioncode}/stations",
            produces = {"application/json"}
    )
    public ResponseEntity<CreditorInstitutionStationEdit> createCreditorInstitutionStation(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                                           @RequestBody @Valid @NotNull CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(creditorInstitutionsService.createCreditorInstitutionStation(creditorInstitutionCode, creditorInstitutionStationEdit));
    }

    @Operation(summary = "Update a relation between creditor institution and station", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionStationEdit.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @PutMapping(value = "/{creditorinstitutioncode}/stations/{stationcode}", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionStationEdit> updateCreditorInstitutionStation(@Size(min = 1, max = 50) @Parameter(description = "The fiscal code of the Organization to update", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                                                       @Size(max = 50) @Parameter(description = "station code.", required = true) @PathVariable("stationcode") String stationCode,
                                                                                @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true) @RequestBody @Valid @NotNull CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        return ResponseEntity.ok(creditorInstitutionsService.updateCreditorInstitutionStation(creditorInstitutionCode, stationCode, creditorInstitutionStationEdit));
    }


    @Operation(summary = "Delete a relation between creditor institution and station", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @DeleteMapping(value = "/{creditorinstitutioncode}/stations/{stationcode}", produces = {"application/json"})
    public ResponseEntity<Void> deleteCreditorInstitutionStation(@Size(min = 1, max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode,
                                                          @Size(max = 50) @Parameter(description = "station code.", required = true) @PathVariable("stationcode") String stationCode) {
        creditorInstitutionsService.deleteCreditorInstitutionStation(creditorInstitutionCode, stationCode);
        return ResponseEntity.ok().build();
    }


    /**
     * GET /{creditorinstitutioncode}/ibans : Get creditor institution ibans
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get creditor institution ibans", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Ibans.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{creditorinstitutioncode}/ibans",
            produces = {"application/json"}
    )
    public ResponseEntity<Ibans> getCreditorInstitutionsIbans(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode) {
        return ResponseEntity.ok(creditorInstitutionsService.getCreditorInstitutionsIbans(creditorInstitutionCode));
    }

}
