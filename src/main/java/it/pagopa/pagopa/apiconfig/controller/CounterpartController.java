package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.CounterpartTables;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.service.CounterpartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@RestController()
@RequestMapping(path = "/counterparttables")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class CounterpartController {

    @Autowired
    CounterpartService counterpartService;


    @Operation(summary = "Get the counterparties table", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CounterpartTables.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    public ResponseEntity<CounterpartTables> getCounterpartTables(
            @Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page) {
        return ResponseEntity.ok(counterpartService.getCounterpartTables(limit, page));
    }


    @Operation(summary = "Download a XML file containing the details of a counterpart table", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(schema = @Schema(implementation = Resource.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{idcounterparttable}",
            produces = {"application/xml", "application/json"}
    )
    public ResponseEntity<Resource> getCounterpartTable(
            @Parameter(description = "Id counterpart table", required = true) @PathVariable("idcounterparttable") String idCounterpartTable,
            @Parameter(description = "Creditor institution code", required = true) @RequestParam("creditorinstitutioncode") String creditorInstitutionCode) {
        byte[] file = counterpartService.getCounterpartTable(idCounterpartTable, creditorInstitutionCode);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(file.length)
                .body(new ByteArrayResource(file));
    }


}
