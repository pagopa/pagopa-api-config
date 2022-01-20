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
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

/**
 * Author: Francesco Cesareo
 * Email: cesareo.francesco@gmail.com
 */

@RestController()
@RequestMapping(path = "/configuration")
@Tag(name = "Configuration", description = "Everything about Configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    /**
     * GET /configuration/keys : Get list of configuration keys
     *
     * @return OK. (status code 200)
     * or Service unavailable (status code 500)
     */
    @Operation(summary = "Get list of configuration keys", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigurationKeys.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/keys", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getConfigurationKeys() {
        return ResponseEntity.ok(configurationService.getConfigurationKeys());
    }
}
