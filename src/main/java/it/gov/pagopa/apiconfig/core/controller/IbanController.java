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
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.*;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Ibans", description = "Everything about Iban")
@Validated
public class IbanController {

  @Autowired private CreditorInstitutionsService creditorInstitutionsService;

  @Autowired private IbanService ibansService;

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
        "Iban",
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
      value = "/{creditorinstitutioncode}/ibans",
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
   * GET /creditorinstitutions/{creditorinstitutioncode}/ibans/enhanced?label={value} : Get creditor
   * institution ibans
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param limit Number of elements on one page. Default = 50
   * @param page Page number. Page value starts from 0
   * @param filterByLabel label for iban filtering
   * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
   *     code 500)
   */
  @Operation(
      summary = "Get creditor institution ibans list",
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
      value = "/{creditorinstitutioncode}/ibans/list",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<IbansEnhanced> getIbans(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam(required = false, defaultValue = "0")
          Integer page,
      @Size(max = 50)
          @Parameter(
              description = "The fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
      @NotNull @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
      String creditorInstitutionCode,
      @RequestParam(required = false, name = "label") @Parameter(description = "Filter by label")
          String filterByLabel) {

    return ResponseEntity.ok(ibansService.getIbans(creditorInstitutionCode, limit, page, filterByLabel));
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
      value = "/{creditorinstitutioncode}/ibans",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<IbanEnhanced> createCreditorInstitutionsIbans(
      @Size(max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @RequestBody @Valid @NotNull IbanEnhanced iban) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ibansService.createIban(creditorInstitutionCode, iban));
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
      value = "/{creditorinstitutioncode}/ibans/{ibanId}",
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
    return ResponseEntity.ok(ibansService.updateIban(creditorInstitutionCode, ibanCode, iban));
  }

  /**
   * DELETE /{creditorinstitutioncode}/iban/{ibanId} : Delete a specific creditor institution iban
   *
   * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
   *     (required)
   * @param ibanValue Value of the Iban to delete. (required)
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
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
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
      value = "/{creditorinstitutioncode}/ibans/{ibanValue}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<String> deleteCreditorInstitutionsIban(
      @Size(max = 50)
          @Parameter(
              description = "Organization fiscal code, the fiscal code of the Organization.",
              required = true)
          @PathVariable("creditorinstitutioncode")
          String creditorInstitutionCode,
      @Size(max = 50)
          @Parameter(description = "Value of the Iban to be deleted", required = true)
          @PathVariable("ibanValue")
          String ibanValue) {
    return ResponseEntity.ok(ibansService.deleteIban(creditorInstitutionCode, ibanValue));
  }
  
  @Operation(
	      summary = "Upload a zip file containing the details of multiple ibans to create",
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
	      value = "/ibans",
	      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> massiveCreateIbans(
	      @NotNull
	          @Parameter(
	              description = "Zip file containing IBANs to create",
	              required = true,
	              content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
	          @RequestParam("file")
	          MultipartFile file) {
	    ibansService.createMassiveIbans(file);
	    return ResponseEntity.status(HttpStatus.CREATED).build();
	  }

    @Operation(
            summary =
                    "Upload a CSV file containing the details of multiple ibans to create",
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
    @PostMapping(
            value = "/ibans/csv",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<Void> massiveCreateIbansCsv(
            @NotNull
            @Parameter(
                    description = "CSV file regarding various Ibans actions",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestParam("file")
            MultipartFile file) {
        ibansService.createMassiveIbansByCsv(file);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(
            summary = "Create or update a label to be associated to IBANs",
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
                            description = "OK",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = IbanLabel.class))),
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
            value = "/ibans/labels",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<IbanLabel> upsertIbanLabel(@RequestBody @Valid @NotNull IbanLabel ibanLabel) {
        return ResponseEntity.ok(ibansService.upsertIbanLabel(ibanLabel));
    }
}
