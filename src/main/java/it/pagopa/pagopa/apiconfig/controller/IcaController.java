package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.ProblemJson;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.service.IcaService;
import it.pagopa.pagopa.apiconfig.service.StorageService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping(path = "/icas")
@Tag(name = "Creditor Institutions", description = "Everything about Creditor Institution")
public class IcaController {

    @Autowired
    IcaService icaService;

    @Autowired
    StorageService storageService;

    @Operation(summary = "Get the list of ICAs", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Icas.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    public ResponseEntity<Icas> getIcas(
            @Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page) {
        return ResponseEntity.ok(icaService.getIcas(limit, page));
    }


    @Operation(summary = "Download a XML file containing the details of an ICA", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(schema = @Schema(implementation = Resource.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "/{idica}",
            produces = {"application/xml", "application/json"}
    )
    public ResponseEntity<Resource> getIca(@Parameter(description = "Id ICA", required = true) @PathVariable("idica") String idIca,
                                           @Parameter(description = "Creditor institution code", required = true) @RequestParam("creditorinstitutioncode") String creditorInstitutionCode) {
        byte[] file = icaService.getIca(idIca, creditorInstitutionCode);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .contentLength(file.length)
                .body(new ByteArrayResource(file));
    }

    @Operation(summary = "Validate XML against XSD", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Creditor Institutions",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @PostMapping(
            value = "/xsd",
            produces = {"application/json"}
    )
    public ResponseEntity<Map> checkXSD(@Parameter(description = "XML file regarding ICA to check", required = true) @RequestParam("file") MultipartFile file) {
        File xml = storageService.store(file);

        Map response = icaService.verifyXSD(xml);

        try {
            FileUtils.forceDelete(xml);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }


}
