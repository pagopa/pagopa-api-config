package it.gov.pagopa.apiconfig.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.apiconfig.model.ProblemJson;
import it.gov.pagopa.apiconfig.service.MassiveLoadingService;

@RestController()
@RequestMapping(path = "/batchoperation")
@Tag(name = "Batch Operation", description = "Everything about Batch Operation")
@Validated
public class MassiveLoadingController {

  @Autowired MassiveLoadingService massiveLoadingService;

  @Operation(
      summary =
          "Update a CSV file containing the relationship between Creditor Institution and Station",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {
        "Massive Loading",
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
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
  @PostMapping(
      value = "/creditorinstitution-station/loading",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> manageCIStationRelationship(
      @NotNull
          @Parameter(
              description = "CSV file regarding CI-Station relationship to manage",
              required = true,
              content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
          @RequestParam("file")
          MultipartFile file) {
    massiveLoadingService.manageCIStation(file);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(
      summary = "Massive migration of the Station-CI relations",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {
        "Creditor Institutions",
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
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
  @PostMapping(
      value = "/creditorinstitution-station/migration",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> massiveMigration(
      @NotNull
          @Parameter(
              description = "CSV file regarding relations to migrate",
              required = true,
              content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
          @RequestParam("file")
          MultipartFile file) {
    massiveLoadingService.massiveMigration(file);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}
