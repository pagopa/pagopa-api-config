package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.EncodingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class EncodingsController {

    @Autowired
    private EncodingsService encodingsService;


    /**
     * GET /creditorinstitutions/{creditorinstitutioncode}/encodings : Get creditor institution encodings
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization. (required)
     * @return OK. (status code 200)
     * or Forbidden client error status. (status code 403)
     * or Not Found (status code 404)
     * or Too many requests (status code 429)
     * or Service unavailable. (status code 500)
     */
    @Operation(summary = "Get creditor institution encodings", security = {@SecurityRequirement(name = "ApiKey")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreditorInstitutionEncodings.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{creditorinstitutioncode}/encodings",
            produces = {"application/json"}
    )
    public ResponseEntity<CreditorInstitutionEncodings> getCreditorInstitutionEncodings(@Size(max = 50) @Parameter(description = "Organization fiscal code, the fiscal code of the Organization.", required = true) @PathVariable("creditorinstitutioncode") String creditorInstitutionCode) {
        return ResponseEntity.ok(encodingsService.getCreditorInstitutionEncodings(creditorInstitutionCode));
    }


}
