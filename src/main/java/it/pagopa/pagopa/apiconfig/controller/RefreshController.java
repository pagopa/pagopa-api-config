package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.RefreshService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/refresh")
@Tag(name = "Refresh Operation", description = "Refresh and trigger job for node configuration")
@Validated
public class RefreshController {

    @Autowired
    private RefreshService refreshService;

    /**
     * GET /refresh/job/{jobtype} : Get job trigger activation
     *
     * @return OK. (status code 200) or Service unavailable (status code 500)
     */
    @Operation(summary = "Job trigger activation ", security = {
        @SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {
        "Job Trigger"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/job/{jobtype}", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getJobTrigger(
        @Parameter(description = "Job Trigger", required = true) @PathVariable("jobtype") JobTrigger jobType
    ) {
        return ResponseEntity.ok(refreshService.jobTrigger(jobType));
    }

    /**
     * GET /refresh/config : Get global configuration refresh activation
     *
     * @return OK. (status code 200) or Service unavailable (status code 500)
     */
    @Operation(summary = "Global Refresh Configuration activation: for all domains", security = {
        @SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Refresh Config"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/config", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getRefreshGlobalConfig() {
        return ResponseEntity.ok(refreshService.refreshConfig(ConfigurationDomain.GLOBAL));
    }

    /**
     * GET /refresh/config/{configtype} : Get domain configuration refresh activation
     *
     * @return OK. (status code 200) or Service unavailable (status code 500)
     */
    @Operation(summary = "Refresh Configuration activation for a specific domain", security = {
        @SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Refresh Config domains"})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
        @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/config/{configtype}", produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<String> getRefreshConfig(
        @Parameter(description = "Configuration domain", required = true) @PathVariable("configtype") ConfigurationDomain configtype) {
        return ResponseEntity.ok(refreshService.refreshConfig(configtype));
    }
}
