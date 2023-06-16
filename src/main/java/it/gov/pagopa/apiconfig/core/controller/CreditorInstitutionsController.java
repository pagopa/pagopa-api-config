package it.gov.pagopa.apiconfig.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.apiconfig.core.model.ProblemJson;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationList;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutions;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionsView;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterPaView;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
@Validated
public class CreditorInstitutionsController {

  @Autowired private CreditorInstitutionsService creditorInstitutionsService;

  /**
   * GET /creditorinstitutions : Get paginated list of creditor institutions
   *
   * @param limit Number of elements on one page. Default = 50
   * @param page Page number. Page value starts from 0
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get paginated list of creditor institutions",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutions.class))),
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
      value = "",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutions> getCreditorInstitutions(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page,
      @RequestParam(required = false, name = "code") @Parameter(description = "Filter by code")
          String filterByCode,
      @RequestParam(required = false, name = "name") @Parameter(description = "Filter by name")
          String filterByName,
      @RequestParam(required = false, name = "orderby", defaultValue = "CODE")
          @Parameter(description = "Order by code or name")
          Order.CreditorInstitution orderBy,
      @RequestParam(required = false, name = "ordering", defaultValue = "DESC")
          @Parameter(description = "Direction of ordering")
          Sort.Direction ordering) {
    return ResponseEntity.ok(
        creditorInstitutionsService.getCreditorInstitutions(
            limit,
            page,
            CommonUtil.getFilterAndOrder(filterByCode, filterByName, orderBy, ordering)));
  }

  /**
   * GET /creditorinstitutions/{creditorinstitutioncode} : Get creditor institution details
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get creditor institution details",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionDetails.class))),
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
      value = "/{creditorinstitutioncode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionDetails> getCreditorInstitution(
      @Size(min = 1, max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode) {
    return ResponseEntity.ok(
        creditorInstitutionsService.getCreditorInstitution(creditorInstitutionCode));
  }

  @Operation(
      summary = "Create creditor institution",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionDetails.class))),
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
      value = "",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionDetails> createCreditorInstitution(
      @RequestBody @Valid @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(creditorInstitutionsService.createCreditorInstitution(creditorInstitutionDetails));
  }

  @Operation(
      summary = "Update creditor institution",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionDetails.class))),
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
      value = "/{creditorinstitutioncode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionDetails> updateCreditorInstitution(
      @Size(min = 1, max = 50)
          @Parameter(description = "The fiscal code of the Organization to update", required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The values to update of the organization",
              required = true)
          @RequestBody
          @Valid
          @NotNull
          CreditorInstitutionDetails creditorInstitutionDetails) {
    return ResponseEntity.ok(
        creditorInstitutionsService.updateCreditorInstitution(
            creditorInstitutionCode, creditorInstitutionDetails));
  }

  @Operation(
      summary = "Delete creditor institution",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
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
      value = "/{creditorinstitutioncode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteCreditorInstitution(
      @Size(min = 1, max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode) {
    creditorInstitutionsService.deleteCreditorInstitution(creditorInstitutionCode);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /creditorinstitutions/{creditorinstitutioncode}/stations : Get station details and relation
   * info with creditor institution
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get station details and relation info with creditor institution",
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
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionStationList.class))),
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
      value = "/{creditorinstitutioncode}/stations",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionStationList> getCreditorInstitutionStations(
      @Size(max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode) {
    return ResponseEntity.ok(
        creditorInstitutionsService.getCreditorInstitutionStations(creditorInstitutionCode));
  }

  @Operation(
      summary = "Create station details and relation info with creditor institution",
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
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionStationEdit.class))),
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
      value = "/{creditorinstitutioncode}/stations",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionStationEdit> createCreditorInstitutionStation(
      @Size(max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @RequestBody @Valid @NotNull CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            creditorInstitutionsService.createCreditorInstitutionStation(
                creditorInstitutionCode, creditorInstitutionStationEdit));
  }

  @Operation(
      summary = "Update a relation between creditor institution and station",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionStationEdit.class))),
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
      value = "/{creditorinstitutioncode}/stations/{stationcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionStationEdit> updateCreditorInstitutionStation(
      @Size(min = 1, max = 50)
          @Parameter(description = "The fiscal code of the Organization to update", required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
          @RequestBody
          @Valid
          @NotNull
          CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
    return ResponseEntity.ok(
        creditorInstitutionsService.updateCreditorInstitutionStation(
            creditorInstitutionCode, stationCode, creditorInstitutionStationEdit));
  }

  @Operation(
      summary = "Delete a relation between creditor institution and station",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Creditor Institutions"})
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
      value = "/{creditorinstitutioncode}/stations/{stationcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteCreditorInstitutionStation(
      @Size(min = 1, max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode) {
    creditorInstitutionsService.deleteCreditorInstitutionStation(
        creditorInstitutionCode, stationCode);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get view creditor institutions broker station",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {
        "Creditor Institutions Broker Station",
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CreditorInstitutionsView.class))),
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
      value = "/view",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<CreditorInstitutionsView> getCreditorInstitutionsView(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page,
      @RequestParam(required = false, name = "creditorInstitutionCode")
          @Parameter(description = "Filter by creditor institution code")
          String creditorInstitutionCode,
      @RequestParam(required = false, name = "paBrokerCode")
          @Parameter(description = "Filter by pa broker code")
          String paBrokerCode,
      @RequestParam(required = false, name = "stationCode")
          @Parameter(description = "Filter by station code")
          String stationCode,
      @RequestParam(required = false, name = "auxDigit")
          @Parameter(description = "Filter by aux digit")
          Long auxDigit,
      @RequestParam(required = false, name = "applicationCode")
          @Parameter(description = "Filter by application code")
          Long applicationCode,
      @RequestParam(required = false, name = "segregationCode")
          @Parameter(description = "Filter by segregation code")
          Long segregationCode,
      @RequestParam(required = false, name = "mod4") @Parameter(description = "Filter by mod4")
          Boolean mod4) {
    return ResponseEntity.ok(
        creditorInstitutionsService.getCreditorInstitutionsView(
            limit,
            page,
            FilterPaView.builder()
                .creditorInstitutionCode(creditorInstitutionCode)
                .paBrokerCode(paBrokerCode)
                .stationCode(stationCode)
                .auxDigit(auxDigit)
                .applicationCode(applicationCode)
                .segregationCode(segregationCode)
                .mod4(mod4)
                .build()));
  }
}
