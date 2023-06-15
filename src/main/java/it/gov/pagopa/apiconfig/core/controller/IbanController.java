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
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ibans;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
   * GET /creditorinstitutions/{creditorinstitutioncode}/ibans/enhanced?label={value} : Get creditor institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param filterByLabel label for iban filtering
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get creditor institution ibans enhanced",
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
                  schema = @Schema(implementation = IbansEnhanced.class))),
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
      value = "/enhanced",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<IbansEnhanced> getCreditorInstitutionsIbansEnhanced(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode,
      @RequestParam(required = false, name = "label") @Parameter(description = "Filter by label")
      String filterByLabel) {

    return ResponseEntity.ok(ibansService.getCreditorInstitutionsIbansByLabel(creditorInstitutionCode, filterByLabel));
  }

  /**
   * POST /{creditorinstitutioncode}/ibans : Create creditor institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param iban the IBAN object to be added.
   * @return response from computation
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
                  schema = @Schema(implementation = IbanEnhanced.class))),
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
              responseCode = "409",
              description = "Conflict",
              content = @Content(schema = @Schema(implementation = ProblemJson.class))),
          @ApiResponse(
              responseCode = "422",
              description = "Unprocessable Entity",
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
  public ResponseEntity<IbanEnhanced> createCreditorInstitutionsIbans(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode,
      @RequestBody @Valid @NotNull IbanEnhanced iban) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        ibansService.createIban(creditorInstitutionCode, iban));
  }

  /**
   * PUT /{creditorinstitutioncode}/ibans/{ibanId} : Update creditor institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param ibanCode the IBAN code to reference to IBAN object
   * @param iban the IBAN object to be updated.
   * @return response from computation
   */
  @Operation(
      summary = "Update creditor institution ibans",
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
                  schema = @Schema(implementation = IbanEnhanced.class))),
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
              responseCode = "409",
              description = "Conflict",
              content = @Content(schema = @Schema(implementation = ProblemJson.class))),
          @ApiResponse(
              responseCode = "422",
              description = "Unprocessable Entity",
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
  @PutMapping(
      value = "/{ibanId}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<IbanEnhanced> updateCreditorInstitutionsIbans(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode,
      @Size(max = 35)
      @Parameter(
          description = "The IBAN identifier code, used to reference the object.",
          required = true)
      @PathVariable("ibanId")
      String ibanCode,
      @RequestBody @Valid @NotNull IbanEnhanced iban) {
    return ResponseEntity.ok(
        ibansService.updateIban(creditorInstitutionCode, ibanCode, iban));
  }


  /**
   * DELETE /{creditorinstitutioncode}/iban/{ibanId} : Delete a specific creditor institution iban
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param ibanValue Value of the Iban to delete.
   *     (required)
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Delete a creditor institution iban",
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
              responseCode = "200",
              description = "Ok",
              content =
              @Content(
                  mediaType = MediaType.TEXT_PLAIN_VALUE,
                  schema = @Schema(implementation = String.class))),
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
  @DeleteMapping(
      value = "/{ibanValue}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> deleteCreditorInstitutionsIban(
      @Size(max = 50)
      @Parameter(
          description = "Organization fiscal code, the fiscal code of the Organization.",
          required = true)
      @PathVariable("creditorinstitutioncode")
      String creditorInstitutionCode,
      @Size(max = 50)
      @Parameter(
          description = "Value of the Iban to be deleted",
          required = true)
      @PathVariable("ibanValue")
      String ibanValue) {
    return ResponseEntity.ok(
        ibansService.deleteIban(creditorInstitutionCode, ibanValue));
  }
}

