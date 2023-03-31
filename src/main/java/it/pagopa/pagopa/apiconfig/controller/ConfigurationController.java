package it.pagopa.pagopa.apiconfig.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

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
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeyBase;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServer;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServers;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentTypeBase;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentTypes;
import it.pagopa.pagopa.apiconfig.model.configuration.Pdd;
import it.pagopa.pagopa.apiconfig.model.configuration.PddBase;
import it.pagopa.pagopa.apiconfig.model.configuration.Pdds;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfBase;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs;
import it.pagopa.pagopa.apiconfig.service.ConfigurationService;

@RestController()
@RequestMapping(path = "/configuration")
@Tag(name = "Configuration", description = "Everything about Configuration")
@Validated
public class ConfigurationController {

  @Autowired private ConfigurationService configurationService;

  /**
   * GET /configuration/keys : Get list of configuration key
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get list of configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ConfigurationKeys.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/keys",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigurationKeys> getConfigurationKeys() {
    return ResponseEntity.ok(configurationService.getConfigurationKeys());
  }

  /**
   * POST /configuration/keys: Create a configuration key
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Create configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ConfigurationKey.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PostMapping(
      value = "/keys",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigurationKey> createConfigurationKey(
      @RequestBody @Valid @NotNull ConfigurationKey configurationKey) {
    ConfigurationKey configKey = configurationService.createConfigurationKey(configurationKey);
    return ResponseEntity.status(HttpStatus.CREATED).body(configKey);
  }

  /**
   * GET /configuration/keys/category/{category}/key/{key} : Get details of a configuration key
   *
   * @param category Configuration category (required)
   * @param key Configuration key (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get details of configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ConfigurationKey.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/keys/category/{category}/key/{key}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigurationKey> getConfigurationKey(
      @Parameter(description = "Configuration category") @PathVariable("category") String category,
      @Parameter(description = "Configuration key") @PathVariable("key") String key) {
    return ResponseEntity.ok(configurationService.getConfigurationKey(category, key));
  }

  /**
   * PUT /configuration/keys/category/{category}/key/{key} : Update a configuration key
   *
   * @param category Configuration category (required)
   * @param key Configuration key (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Update configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ConfigurationKeyBase.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PutMapping(
      value = "/keys/category/{category}/key/{key}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ConfigurationKey> updateConfigurationKey(
      @Parameter(description = "Configuration category") @PathVariable("category") String category,
      @Parameter(description = "Configuration key") @PathVariable("key") String key,
      @RequestBody @Valid @NotNull ConfigurationKeyBase configurationKey) {
    ConfigurationKey configKey =
        configurationService.updateConfigurationKey(category, key, configurationKey);
    return ResponseEntity.ok(configKey);
  }

  /**
   * DELETE /configuration/keys/category/{category}/key/{key} : Delete a configuration key
   *
   * @param category Configuration category (required)
   * @param key Configuration key (required)
   * @return OK. (status code 200)
   */
  @Operation(
      summary = "Delete configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @DeleteMapping(
      value = "/keys/category/{category}/key/{key}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteConfigurationKey(
      @Parameter(description = "Configuration category") @PathVariable("category") String category,
      @Parameter(description = "Configuration key") @PathVariable("key") String key) {
    configurationService.deleteConfigurationKey(category, key);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /configuration/wfespplugins : Get list of WFESP Plugin configuration
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get list of WFESP Plugin configuration",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WfespPluginConfs.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/wfespplugins",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<WfespPluginConfs> getWfespPlugins() {
    return ResponseEntity.ok(configurationService.getWfespPluginConfigurations());
  }

  /**
   * POST /configuration/wfespplugins : Create a WFESP Plugin configuration
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Create configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WfespPluginConf.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PostMapping(
      value = "/wfespplugins",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<WfespPluginConf> createWfespPlugin(
      @RequestBody @Valid @NotNull WfespPluginConf wfespPluginConf) {
    WfespPluginConf result = configurationService.createWfespPluginConfiguration(wfespPluginConf);
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  /**
   * GET /configuration/wfespplugins/idServPlugin : Get details of a Wfesp plugin
   *
   * @param idServPlugin idServPlugin (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get details of a Wfesp plugin",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WfespPluginConf.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/wfespplugins/{idServPlugin}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<WfespPluginConf> getWfespPlugin(
      @Parameter(description = "idServPlugin") @PathVariable("idServPlugin") String idServPlugin) {
    return ResponseEntity.ok(configurationService.getWfespPluginConfiguration(idServPlugin));
  }

  /**
   * PUT /configuration/wfespplugins/idServPlugin : Update a Wfesp plugin configuration
   *
   * @param idServPlugin idServPlugin (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Update Wfesp plugin configuration",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = WfespPluginConfBase.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PutMapping(
      value = "/wfespplugins/{idServPlugin}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<WfespPluginConf> updateWfespPlugin(
      @Parameter(description = "idServPlugin") @PathVariable("idServPlugin") String idServPlugin,
      @RequestBody @Valid @NotNull WfespPluginConfBase wfespPluginConf) {
    WfespPluginConf wp =
        configurationService.updateWfespPluginConfiguration(idServPlugin, wfespPluginConf);
    return ResponseEntity.ok(wp);
  }

  /**
   * DELETE /configuration/wfespplugins/idServPlugin : Delete a Wfesp plugin configuration
   *
   * @param idServPlugin idServPlugin (required)
   * @return OK. (status code 200)
   */
  @Operation(
      summary = "Delete configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @DeleteMapping(
      value = "/wfespplugins/{idServPlugin}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteWfespPlugin(
      @Parameter(description = "idServPlugin") @PathVariable("idServPlugin") String idServPlugin) {
    configurationService.deleteWfespPluginConfiguration(idServPlugin);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /configuration/pdds : Get list of pdd
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get list of pdd",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Pdds.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/pdds",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Pdds> getPdds() {
    return ResponseEntity.ok(configurationService.getPdds());
  }

  /**
   * POST /configuration/pdds: Create a pdd
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Create pdd",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Pdd.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PostMapping(
      value = "/pdds",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Pdd> createPdd(@RequestBody @Valid @NotNull Pdd pdd) {
    Pdd savedPdd = configurationService.createPdd(pdd);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedPdd);
  }

  /**
   * GET /configuration/pdds/{id_pdd} : Get details of a pdd
   *
   * @param idPdd id (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get details of a pdd",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Pdd.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/pdds/{id_pdd}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Pdd> getPdd(
      @Parameter(description = "Configuration identifier") @PathVariable("id_pdd") String idPdd) {
    return ResponseEntity.ok(configurationService.getPdd(idPdd));
  }

  /**
   * PUT /configuration/pdds/{id_pdd} : Update details of a pdd
   *
   * @param idPdd id (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Update pdd",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PddBase.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PutMapping(
      value = "/pdds/{id_pdd}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Pdd> updatePdd(
      @Parameter(description = "Configuration identifier") @PathVariable("id_pdd") String idPdd,
      @RequestBody @Valid @NotNull PddBase pdd) {
    Pdd updatedPdd = configurationService.updatePdd(idPdd, pdd);
    return ResponseEntity.ok(updatedPdd);
  }

  /**
   * DELETE /configuration/pdds/{id_pdd} : Delete a pdd
   *
   * @param idPdd id (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Delete pdd",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @DeleteMapping(
      value = "/pdds/{id_pdd}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deletePdd(
      @Parameter(description = "Configuration identifier") @PathVariable("id_pdd") String idPdd) {
    configurationService.deletePdd(idPdd);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /configuration/ftpservers : Get list of ftp server
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get list of ftp server",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FtpServers.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/ftpservers",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FtpServers> getFtpServers() {
    return ResponseEntity.ok(configurationService.getFtpServers());
  }

  /**
   * POST /configuration/ftpservers : Create a ftp server
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Create ftp server",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FtpServer.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PostMapping(
      value = "/ftpservers",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FtpServer> createFtpServer(
      @RequestBody @Valid @NotNull FtpServer ftpServer) {
    FtpServer savedFtpserver = configurationService.createFtpServer(ftpServer);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedFtpserver);
  }

  /**
   * GET /configuration/ftpservers/host/{host}/port/{port}/service/{service} : Get details of a ftp
   * server
   *
   * @param host ftp server host (required)
   * @param port ftp server port (required)
   * @param service ftp server service (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get details of ftp server",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FtpServer.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/ftpservers/host/{host}/port/{port}/service/{service}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FtpServer> getFtpServer(
      @Parameter(description = "Host") @PathVariable("host") String host,
      @Parameter(description = "Port") @PathVariable("port") Integer port,
      @Parameter(description = "Service") @PathVariable("service") String service) {
    return ResponseEntity.ok(configurationService.getFtpServer(host, port, service));
  }

  /**
   * PUT /configuration/ftpservers/host/{host}/port/{port}/service/{service} : Update details of a
   * ftp server
   *
   * @param host ftp server host (required)
   * @param port ftp server port (required)
   * @param service ftp server service (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Update configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = FtpServer.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PutMapping(
      value = "/ftpservers/host/{host}/port/{port}/service/{service}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<FtpServer> updateFtpServer(
      @Parameter(description = "Host") @PathVariable("host") String host,
      @Parameter(description = "Port") @PathVariable("port") Integer port,
      @Parameter(description = "Service") @PathVariable("service") String service,
      @RequestBody @Valid @NotNull FtpServer ftpServer) {
    FtpServer savedFtpServer = configurationService.updateFtpServer(host, port, service, ftpServer);
    return ResponseEntity.ok(savedFtpServer);
  }

  /**
   * DELETE /configuration/ftpservers/host/{host}/port/{port}/service/{service} : Delete details of
   * a ftp server
   *
   * @param host ftp server host (required)
   * @param port ftp server port (required)
   * @param service ftp server service (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Delete configuration key",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @DeleteMapping(
      value = "/ftpservers/host/{host}/port/{port}/service/{service}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteFtpServer(
      @Parameter(description = "Host") @PathVariable("host") String host,
      @Parameter(description = "Port") @PathVariable("port") Integer port,
      @Parameter(description = "Service") @PathVariable("service") String service) {
    configurationService.deleteFtpServer(host, port, service);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /configuration/paymenttype : Get list of payment type
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get list of payment type",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentTypes.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/paymenttypes",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PaymentTypes> getPaymentTypes() {
    return ResponseEntity.ok(configurationService.getPaymentTypes());
  }

  /**
   * POST /configuration/paymenttypes : Create a payment type
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Create payment type",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentType.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "409",
            description = "Conflict",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PostMapping(
      value = "/paymenttypes",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PaymentType> createPaymentType(
      @RequestBody @Valid @NotNull PaymentType paymentType) {
    PaymentType createdPaymentType = configurationService.createPaymentType(paymentType);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentType);
  }

  @Operation(
      summary = "Trigger to upload payment types history on AFM Marketplace",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping("/paymenttypes/history")
  public ResponseEntity<Void> uploadHistory() {
    configurationService.syncPaymentTypesHistory();
    return ResponseEntity.ok().build();
  }

  /**
   * GET /configuration/paymenttypes/{paymentTypeCode} : Get details of a payment type
   *
   * @param paymentTypeCode payment type code (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get details of payment type",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentType.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "/paymenttypes/{paymentTypeCode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PaymentType> getPaymentType(
      @Parameter(description = "Payment type code") @PathVariable("paymentTypeCode")
          String paymentTypeCode) {
    return ResponseEntity.ok(configurationService.getPaymentType(paymentTypeCode));
  }

  /**
   * PUT /configuration/paymenttypes/{paymentTypeCode} : Update details of a payment type
   *
   * @param paymentTypeCode payment type code (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Update payment type",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PaymentType.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @PutMapping(
      value = "/paymenttypes/{paymentTypeCode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PaymentType> updatePaymentType(
      @Parameter(description = "Payment type code")
          @PathVariable("paymentTypeCode")
          @Pattern(regexp = "[A-Z]*")
          String paymentTypeCode,
      @RequestBody @Valid @NotNull PaymentTypeBase paymentType) {
    PaymentType savedPaymentType =
        configurationService.updatePaymentType(paymentTypeCode, paymentType);
    return ResponseEntity.ok(savedPaymentType);
  }

  /**
   * DELETE /configuration/paymenttypes/{paymentTypeCode} : Delete details of a payment type
   *
   * @param paymentTypeCode payment type code (required)
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Delete payment type",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Configuration"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema())),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "404",
            description = "Not Found",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class))),
        @ApiResponse(
            responseCode = "429",
            description = "Too many requests",
            content = @Content(schema = @Schema())),
        @ApiResponse(
            responseCode = "500",
            description = "Service unavailable",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @DeleteMapping(
      value = "paymenttypes/{paymentTypeCode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deletePaymentType(
      @Parameter(description = "Payment type code") @PathVariable("paymentTypeCode")
          String paymentTypeCode) {
    configurationService.deletePaymentType(paymentTypeCode);
    return ResponseEntity.ok().build();
  }
}
