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
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController()
@RequestMapping(path = "/configuration")
@Tag(name = "Configuration", description = "Everything about Configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    /**
     * GET /configuration/keys : Get list of configuration key
     *
     * @return OK. (status code 200)
     * or Service unavailable (status code 500)
     */
    @Operation(summary = "Get list of configuration key", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigurationKeys.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/keys", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConfigurationKeys> getConfigurationKeys() {
        return ResponseEntity.ok(configurationService.getConfigurationKeys());
    }

    /**
     * POST /configuration/keys: Create a configuration key
     *
     * @return OK. (status code 200)
     * or Service unavailable (status code 500)
     */
    @Operation(summary = "Create configuration key", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigurationKey.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "409", description = "Conflict", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @PostMapping(value = "/keys", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConfigurationKey> createCreditorInstitution(@RequestBody @Valid @NotNull ConfigurationKey configurationKey) {
        ConfigurationKey configKey = configurationService.createConfigurationKey(configurationKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(configKey);
    }

    /**
     * GET /configuration/keys/category/{category}/key/{key} : Get details of a configuration key
     *
     * @param category Configuration category (required)
     * @param key Configuration key (required)
     * @return OK. (status code 200)
     * or Service unavailable (status code 500)
     */
    @Operation(summary = "Get details of configuration key", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigurationKey.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/keys/category/{category}/key/{key}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConfigurationKey> getConfigurationKey(@Parameter(description = "Configuration category") @PathVariable("category") String category,
                                                                @Parameter(description = "Configuration key") @PathVariable("key") String key) {
        return ResponseEntity.ok(configurationService.getConfigurationKey(category, key));
    }

    /**
     * PUT /configuration/keys/category/{category}/key/{key} : Update a configuration key
     *
     * @param category Configuration category (required)
     * @param key Configuration key (required)
     * @return OK. (status code 200)
     * or Service unavailable (status code 500)
     */
    @Operation(summary = "Update configuration key", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ConfigurationKey.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @PutMapping(value = "/keys/category/{category}/key/{key}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConfigurationKey> updateConfigurationKey(@Parameter(description = "Configuration category") @PathVariable("category") String category,
                                                                      @Parameter(description = "Configuration key") @PathVariable("key") String key,
                                                                      @RequestBody @Valid @NotNull ConfigurationKey configurationKey) {
        ConfigurationKey configKey = configurationService.updateConfigurationKey(category, key, configurationKey);
        return ResponseEntity.ok(configKey);
    }

    /**
     * DELETE /configuration/keys/category/{category}/key/{key} : Delete a configuration key
     *
     * @param category Configuration category (required)
     * @param key Configuration key (required)
     * @return
     */
    @Operation(summary = "Delete configuration key", security = {@SecurityRequirement(name = "ApiKey"), @SecurityRequirement(name = "Authorization")}, tags = {"Configuration"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemJson.class)))})
    @DeleteMapping(value = "/keys/category/{category}/key/{key}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteConfigurationKeys(@Parameter(description = "Configuration category") @PathVariable("category") String category,
                                                        @Parameter(description = "Configuration key") @PathVariable("key") String key) {
        configurationService.deleteConfigurationKey(category, key);
        return ResponseEntity.ok().build();
    }

}
