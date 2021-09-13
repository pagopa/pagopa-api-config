package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import it.pagopa.pagopa.apiconfig.models.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.models.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.CreditorInstitutionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditorInstitutionsController {

    @Autowired
    CreditorInstitutionsService creditorInstitutionsService;

    @Operation(summary = "Get creditor institutions list ", description = "", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Istitutions"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutions.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status."),
            @ApiResponse(responseCode = "429", description = "Too many requests"),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/creditorinstitutions/", produces = {"application/json"})
    public ResponseEntity<CreditorInstitutions> getECs() {
        return ResponseEntity.ok(creditorInstitutionsService.getECs());
    }

}
