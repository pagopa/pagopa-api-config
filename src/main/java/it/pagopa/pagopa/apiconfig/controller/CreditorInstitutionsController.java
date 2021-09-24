package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.*;
import it.pagopa.pagopa.apiconfig.service.CreditorInstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class CreditorInstitutionsController {

    @Autowired
    private CreditorInstitutionsService creditorInstitutionsService;


    /**
     * GET /creditorinstitutions : Get creditor institutions list
     *
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get creditor institutions list. The result is paging", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutions.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutions> getCreditorInstitutions(@Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit, @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page) {
        return ResponseEntity.ok(creditorInstitutionsService.getECs(limit, page));
    }

    /**
     * GET /creditorinstitutions/{organizationfiscalcode} : Get creditor institution details
     *
     * @param organizationFiscalCode Organization fiscal code, the fiscal code of the Organization. (required)
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
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/{organizationfiscalcode}", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionDetails> getCreditorInstitution(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("organizationfiscalcode") String organizationFiscalCode) {
        return ResponseEntity.ok(creditorInstitutionsService.getEC(organizationFiscalCode));
    }


    /**
     * GET /creditorinstitutions/{organizationfiscalcode}/stations : Get station details and relation info with CI
     *
     * @param organizationfiscalcode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get station details and relation info with CI", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StationCIList.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{organizationfiscalcode}/stations",
            produces = {"application/json"}
    )
    public ResponseEntity<StationCIList> getStationsCI(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("organizationfiscalcode") String organizationfiscalcode) {
        return null;

    }

    /**
     * GET /creditorinstitutions/{organizationfiscalcode}/encodings : Get creditor institution encodings
     *
     * @param organizationfiscalcode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get creditor institution encodings", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionEncodings.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{organizationfiscalcode}/encodings",
            produces = {"application/json"}
    )
    public ResponseEntity<CreditorInstitutionEncodings> getCreditorInstitutionEncodings(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("organizationfiscalcode") String organizationfiscalcode) {
        return null;

    }


    /**
     * GET /{organizationfiscalcode}/ibans : Get creditor institution ibans
     *
     * @param organizationfiscalcode Organization fiscal code, the fiscal code of the Organization. (required)
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
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{organizationfiscalcode}/ibans",
            produces = {"application/json"}
    )
    public ResponseEntity<Ibans> getCreditorInstitutionsIbans(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("organizationfiscalcode") String organizationfiscalcode) {
        return null;

    }

}
