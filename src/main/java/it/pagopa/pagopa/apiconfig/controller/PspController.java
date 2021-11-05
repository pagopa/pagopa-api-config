package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.ProblemJson;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.service.PspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@RestController()
@RequestMapping(path = "/paymentserviceproviders")
@Tag(name = "Payment Service Providers", description = "Everything about Payment Service Providers")
public class PspController {

    @Autowired
    PspService pspService;

    @Operation(summary = "Get paginated list of Payment Service Providers", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Payment Service Providers",})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentServiceProviders.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(
            value = "",
            produces = {"application/json"}
    )
    public ResponseEntity<PaymentServiceProviders> getPaymentServiceProviders(
            @Positive @Parameter(description = "Number of elements on one page. Default = 50") @RequestParam(required = false, defaultValue = "50") Integer limit,
            @Positive @Parameter(description = "Page number. Page value starts from 0", required = true) @RequestParam Integer page) {
        return ResponseEntity.ok(pspService.getPaymentServiceProviders(limit, page));
    }


    @Operation(summary = "Get payment service provider details", security = {@SecurityRequirement(name = "ApiKey")}, tags = {"Payment Service Providers"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentServiceProviderDetails.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden client error status.", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class))),
            @ApiResponse(responseCode = "429", description = "Too many requests", content = @Content(mediaType = "application/json", schema = @Schema())),
            @ApiResponse(responseCode = "500", description = "Service unavailable.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemJson.class)))})
    @GetMapping(value = "/{pspcode}", produces = {"application/json"})
    public ResponseEntity<PaymentServiceProviderDetails> getPaymentServiceProvider(@NotBlank @Size(min = 1, max = 50) @Parameter(description = "Code of the payment service provider", required = true) @PathVariable("pspcode") String pspCode) {
        return ResponseEntity.ok(pspService.getPaymentServiceProvider(pspCode));
    }
}
