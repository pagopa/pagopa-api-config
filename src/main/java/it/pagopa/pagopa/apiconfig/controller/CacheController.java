package it.pagopa.pagopa.apiconfig.controller;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.model.configuration.CacheVersions;
import it.pagopa.pagopa.apiconfig.service.CacheService;

@RestController()
@RequestMapping(path = "/cache")
@Tag(name = "Cache", description = "Everything about Cache")
@Validated
public class CacheController {

  @Autowired private CacheService cacheService;

  @Operation(
      summary = "Get cache versions",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CacheVersions.class))),
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
            content = @Content(schema = @Schema(implementation = ProblemJson.class))),
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
      value = "/versions",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CacheVersions> getCacheVersions(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 3")
          @RequestParam(required = false, defaultValue = "3")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0")
          @RequestParam(required = false, defaultValue = "0")
          Integer page) {
    return ResponseEntity.ok(cacheService.getCacheVersions(page, limit));
  }

  @Operation(
      summary = "Get cache by version",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Resource.class))),
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
            content = @Content(schema = @Schema(implementation = ProblemJson.class))),
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
      value = "/versions/{version}",
      produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Resource> getCacheByVersion(
      @NotBlank @Parameter(description = "Node version", required = true) @PathVariable("version")
          String version) {
    Cache cache = cacheService.getCache(version);
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Cache-Id", cache.getId());
    headers.add("Content-Disposition", "attachment;filename=cache.zip");
    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .contentLength(cache.getCache().length)
        .body(new ByteArrayResource(cache.getCache()));
  }

  @Operation(
      summary = "Get cache id by version",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema =
                        @Schema(
                            implementation =
                                it.pagopa.pagopa.apiconfig.model.configuration.Cache.class))),
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
            content = @Content(schema = @Schema(implementation = ProblemJson.class))),
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
      value = "/versions/{version}/id",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<it.pagopa.pagopa.apiconfig.model.configuration.Cache> getCacheId(
      @NotBlank @Parameter(description = "Node version", required = true) @PathVariable("version")
          String version) {
    Cache cache = cacheService.getCache(version);

    return ResponseEntity.ok(
        it.pagopa.pagopa.apiconfig.model.configuration.Cache.builder()
            .id(cache.getId())
            .version(cache.getVersion())
            .build());
  }
}
