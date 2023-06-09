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
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanV2;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ibans;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions/{creditorinstitutioncode}/ibans")
@Tag(name = "Ibans", description = "Everything about Iban")
@Validated
public class IbanController {

  @Autowired
  private CreditorInstitutionsService creditorInstitutionsService;

  @Autowired
  private IbanService ibansService;

  /**
   * GET /creditorinstitutions/{creditorinstitutioncode}/ibans : Get creditor institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get creditor institution ibans",
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
                  schema = @Schema(implementation = Ibans.class))),
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
      value = "",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Ibans> getCreditorInstitutionsIbans(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode) {
    return ResponseEntity.ok(
        creditorInstitutionsService.getCreditorInstitutionsIbans(creditorInstitutionCode));
  }

  /**
   * GET /{creditorinstitutioncode}/ibans : Get creditor institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Create creditor institution ibans",
      security = {
          @SecurityRequirement(name = "ApiKey"),
          @SecurityRequirement(name = "Authorization")
      },
      tags = {
          "Iban",
      })
  @ApiResponses(
      value = {
          @ApiResponse(
              responseCode = "201",
              description = "Created",
              content =
              @Content(
                  mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Ibans.class))),
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
  @PostMapping(
      value = "",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<IbanV2> createCreditorInstitutionsIbans(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode,
      IbanV2 iban) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ibansService.createIban(creditorInstitutionCode, iban));
  }

}