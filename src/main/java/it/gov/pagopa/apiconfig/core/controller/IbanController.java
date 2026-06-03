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
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ibans;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import it.gov.pagopa.apiconfig.core.service.IbanService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/creditorinstitutions")
@Tag(name = "Ibans", description = "Everything about Iban")
@Validated
public class IbanController {

    private final CreditorInstitutionsService creditorInstitutionsService;

    private final IbanService ibansService;

    public IbanController(CreditorInstitutionsService creditorInstitutionsService, IbanService ibansService) {
        this.creditorInstitutionsService = creditorInstitutionsService;
        this.ibansService = ibansService;
    }

    /**
     * GET /creditorinstitutions/{creditorinstitutioncode}/ibans : Get creditor institution ibans
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
     *                                (required)
     * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
     * code 500)
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
            String creditorInstitutionCode
    ) {
        return ResponseEntity.ok(
                creditorInstitutionsService.getCreditorInstitutionsIbans(creditorInstitutionCode));
    }

    /**
     * GET /creditorinstitutions/{creditorinstitutioncode}/ibans/list : Get creditor institution ibans
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
     *                                (required)
     * @param limit                   Number of elements on one page. Default = 50
     * @param page                    Page number. Page value starts from 0
     * @param filterByLabel           label for iban filtering
     * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
     * code 500)
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
            String filterByLabel,
            @RequestParam(required = false, name = "iban") @Parameter(description = "Filter by iban") String filterByIban
    ) {

        return ResponseEntity
                .ok(ibansService.getIbans(creditorInstitutionCode, limit, page, filterByLabel, filterByIban));
    }

    /**
     * POST /{creditorinstitutioncode}/ibans : Create creditor institution ibans
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
     *                                (required)
     * @param iban                    the IBAN object to be added.
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
            @RequestBody @Valid @NotNull IbanEnhanced iban
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ibansService.createIban(creditorInstitutionCode, iban));
    }

    /**
     * PUT /{creditorinstitutioncode}/ibans/{ibanId} : Update creditor institution ibans
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
     *                                (required)
     * @param ibanCode                the IBAN code to reference to IBAN object
     * @param iban                    the IBAN object to be updated.
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
            @RequestBody @Valid @NotNull IbanEnhanced iban
    ) {
        return ResponseEntity.ok(ibansService.updateIban(creditorInstitutionCode, ibanCode, iban));
    }

    /**
     * DELETE /{creditorinstitutioncode}/iban/{ibanId} : Delete a specific creditor institution iban
     *
     * @param creditorInstitutionCode Organization fiscal code, the fiscal code of the Organization.
     *                                (required)
     * @param ibanValue               Value of the Iban to delete. (required)
     * @return OK. (status code 200) or Not Found (status code 404) or Service unavailable (status
     * code 500)
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
            String ibanValue
    ) {
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
            MultipartFile file
    ) {
        ibansService.createMassiveIbans(file);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Massive insert/update/delete of IBANs via CSV file",
            description =
                    """
                            Upload a CSV file (UTF-8, comma-separated) containing the details of multiple IBANs to insert, update or delete in a single bulk operation.
                            
                            ### CSV columns
                            | Column | Required | Description |
                            |---|---|---|
                            | `iddominio` | always | Fiscal code of the Creditor Institution (11 digits) |
                            | `iban` | always | IBAN code |
                            | `operazione` | always | Operation type: `I` (insert), `U`/`M` (update), `D`/`C` (delete) |
                            | `descrizione` | optional | IBAN description (used on insert/update) |
                            | `dataattivazioneiban` | required on **insert**, forbidden on update/delete | IBAN activation/validity date (`yyyy-MM-dd`) |
                            | `datascadenzaiban` | optional on insert/update, forbidden on delete | IBAN due date (`yyyy-MM-dd`); on insert defaults to *today + 1 year* if omitted |
                            
                            ### Rules per operation
                            - **Insert (`I`)**: `dataattivazioneiban` is mandatory; `descrizione` and `datascadenzaiban` are optional.
                            - **Update (`U` / `M`)**: at least one of `descrizione` or `datascadenzaiban` must be provided; `dataattivazioneiban` must NOT be provided.
                            - **Delete (`D` / `C`)**: only `iddominio`, `iban` and `operazione` are allowed; `descrizione`, `dataattivazioneiban` and `datascadenzaiban` must NOT be provided.
                            - The same IBAN cannot appear more than once in the file.
                            
                            ### CSV example
                            ```csv
                            iddominio,iban,operazione,descrizione,dataattivazioneiban,datascadenzaiban
                            77777777777,IT60X0542811101000000123456,I,Conto principale,2025-01-01,2030-01-01
                            77777777777,IT60X0542811101000000123457,I,,2025-02-01,
                            77777777777,IT60X0542811101000000123458,U,Nuova descrizione,,2031-12-31
                            77777777777,IT60X0542811101000000123459,M,,,2032-06-30
                            77777777777,IT60X0542811101000000123460,D,,,
                            77777777777,IT60X0542811101000000123461,C,,,
                            ```
                            """,
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
                            description = "OK - all rows have been processed successfully",
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
                            responseCode = "404",
                            description = "Not Found",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict",
                            content =
                            @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemJson.class))),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
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
                    description = "CSV file describing the IBAN operations to perform (insert/update/delete). " +
                            "See the operation description for the expected columns and per-operation rules.",
                    required = true,
                    content = @Content(mediaType = "text/csv"))
            @RequestParam("file")
            MultipartFile file
    ) {
        ibansService.processMassiveIbanOperationByCsv(file);
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
