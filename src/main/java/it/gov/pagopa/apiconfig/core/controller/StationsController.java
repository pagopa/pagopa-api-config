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
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitutions;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Stations;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.core.service.StationsService;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/stations")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
@Validated
public class StationsController {

  @Autowired StationsService stationsService;

  /**
   * GET /stations : Get paginated list of stations
   *
   * @param limit Number of elements on one page. Default = 50
   * @param page Page number. Page value starts from 0
   * @param brokerCode Filter by broker
   * @param creditorInstitutionCode Filter by creditor institution
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Get paginated list of stations",
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
                    schema = @Schema(implementation = Stations.class))),
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
  public ResponseEntity<Stations> getStations(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page,
      @Parameter(description = "Filter by broker")
          @RequestParam(name = "brokercode", required = false)
          String brokerCode,
      @Parameter(description = "Filter by broker description")
          @RequestParam(required = false, name = "brokerdescription")
          String brokerDescription,
      @Parameter(description = "Filter by creditor institution")
          @RequestParam(name = "creditorinstitutioncode", required = false)
          String creditorInstitutionCode,
      @RequestParam(required = false, name = "code") @Parameter(description = "Filter by code")
          String filterByCode,
      @RequestParam(required = false, name = "ordering", defaultValue = "DESC")
          @Parameter(description = "Direction of ordering. Results are ordered by code")
          Sort.Direction ordering) {
    return ResponseEntity.ok(
        stationsService.getStations(
            limit,
            page,
            brokerCode,
            brokerDescription,
            creditorInstitutionCode,
            CommonUtil.getFilterAndOrder(filterByCode, null, Order.Station.CODE, ordering)));
  }

  /**
   * GET /stations/{stationcode} : Get station details
   *
   * @param stationCode station code. (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get station details",
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
                    schema = @Schema(implementation = StationDetails.class))),
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
      value = "/{stationcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<StationDetails> getStation(
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode) {
    return ResponseEntity.ok(stationsService.getStation(stationCode));
  }

  @Operation(
      summary = "Create a station",
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
                    schema = @Schema(implementation = StationDetails.class))),
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
  public ResponseEntity<StationDetails> createStation(
      @RequestBody @Valid @NotNull StationDetails stationDetails) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(stationsService.createStation(stationDetails));
  }

  @Operation(
      summary = "Update a station",
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
                    schema = @Schema(implementation = StationDetails.class))),
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
      value = "/{stationcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<StationDetails> updateStation(
      @Size(max = 50)
          @Parameter(description = "station code", required = true)
          @PathVariable("stationcode")
          String stationCode,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The values to update of the station",
              required = true)
          @RequestBody
          @Valid
          @NotNull
          StationDetails stationDetails) {
    return ResponseEntity.ok(stationsService.updateStation(stationCode, stationDetails));
  }

  @Operation(
      summary = "Delete a station",
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
            content = @Content(schema = @Schema())),
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
      value = "/{stationcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteStation(
      @Size(max = 50)
          @Parameter(description = "station code", required = true)
          @PathVariable("stationcode")
          String stationCode) {
    stationsService.deleteStation(stationCode);
    return ResponseEntity.ok().build();
  }

  /**
   * GET /stations/{stationcode}/creditorinstitutions : Get station creditor institutions
   *
   * @param stationCode station code. (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get station creditor institution list",
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
                    schema = @Schema(implementation = StationCreditorInstitutions.class))),
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
      value = "/{stationcode}/creditorinstitutions",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<StationCreditorInstitutions> getStationCreditorInstitutions(
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode,
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page) {
    return ResponseEntity.ok(
        stationsService.getStationCreditorInstitutions(stationCode, limit, page));
  }

  @Operation(
      summary = "Download a CSV with station creditor institution list",
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
      value = "/{stationcode}/creditorinstitutions/csv",
      produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
  @Cacheable(value = "getStationCreditorInstitutionsCSV")
  public ResponseEntity<Resource> getStationCreditorInstitutionsCSV(
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode) {
    byte[] file = stationsService.getStationCreditorInstitutionsCSV(stationCode);
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(file.length)
        .body(new ByteArrayResource(file));
  }

  /**
   * GET /stations/{stationcode}/creditorinstitutions/{creditorinstitutioncode} : Get station-ec
   * relation
   *
   * @param stationCode station code. (required)
   * @param creditorInstitutionCode organization fiscal code. (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get station creditor institution relation",
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
                    schema = @Schema(implementation = StationCreditorInstitutions.class))),
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
      value = "/{stationcode}/creditorinstitutions/{creditorinstitutioncode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<StationCreditorInstitution> getStationCreditorInstitutionRelation(
      @Size(max = 50)
          @Parameter(description = "station code.", required = true)
          @PathVariable("stationcode")
          String stationCode,
      @Size(min = 1, max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode) {
    return ResponseEntity.ok(
        stationsService.getStationCreditorInstitutionRelation(
            stationCode, creditorInstitutionCode));
  }

  /**
   * GET /stations/csv : Get all the stations inside a csv file
   *
   * @return OK. (status code 200) or Service unavailable (status code 500)
   */
  @Operation(
      summary = "Download a CSV with all the stations in the system",
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
      value = "/csv",
      produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Resource> getStationsCSV() {
    byte[] file = stationsService.getStationsCSV();
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(file.length)
        .body(new ByteArrayResource(file));
  }
}
