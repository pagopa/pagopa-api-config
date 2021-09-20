package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.CreditorInstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
public class CreditorInstitutionsController {

    @Autowired
    private CreditorInstitutionsService creditorInstitutionsService;

    @Operation(summary = "Get creditor institutions list ", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Istitutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutions.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutions> getECs() {
        return ResponseEntity.ok(creditorInstitutionsService.getECs());
    }

    @Operation(summary = "Get creditor institution details ", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Istitutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status."),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "429", description = "Too many requests"),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/{organizationfiscalcode}", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutionDetails> getEC(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("organizationfiscalcode") String organizationFiscalCode) {
        return ResponseEntity.ok(creditorInstitutionsService.getEC(organizationFiscalCode));
    }


}
