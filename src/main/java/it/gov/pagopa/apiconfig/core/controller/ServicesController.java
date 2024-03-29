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
import it.gov.pagopa.apiconfig.core.model.psp.Service;
import it.gov.pagopa.apiconfig.core.model.psp.Services;
import it.gov.pagopa.apiconfig.core.service.ServicesService;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(path = "/services")
@Tag(name = "Payment Service Providers", description = "Everything about Payment Service Providers")
@Validated
public class ServicesController {

  @Autowired ServicesService servicesService;

  @Operation(
      summary = "Get paginated list of services",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {
        "Payment Service Providers",
      })
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Services.class))),
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
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemJson.class)))
      })
  @GetMapping(
      value = "",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Services> getServices(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page,
      @Pattern(regexp = "[A-Z0-9_]{6,14}")
          @Parameter()
          @RequestParam(required = false, name = "pspcode")
          String pspCode,
      @Parameter() @RequestParam(required = false, name = "brokerpspcode") String brokerPspCode,
      @Parameter() @RequestParam(required = false, name = "channelcode") String channelCode,
      @Parameter() @RequestParam(required = false, name = "paymentmethodchannel")
          Long paymentMethodChannel,
      @Parameter() @RequestParam(required = false, name = "paymenttypecode") String paymentTypeCode,
      @Parameter() @RequestParam(required = false, name = "pspflagftamp") Boolean pspFlagStamp,
      @Parameter() @RequestParam(required = false, name = "channelapp") Boolean channelApp,
      @Parameter() @RequestParam(required = false, name = "onus") Boolean onUs,
      @Parameter() @RequestParam(required = false, name = "flagio") Boolean flagIo,
      @Parameter() @RequestParam(required = false, name = "flowid") String flowId,
      @Parameter() @RequestParam(required = false, name = "minimumamount") Double minimumAmount,
      @Parameter() @RequestParam(required = false, name = "maximumamount") Double maximumAmount,
      @Parameter() @RequestParam(required = false, name = "languagecode", defaultValue = "IT")
          Service.LanguageCode languageCode,
      @Parameter() @RequestParam(required = false, name = "conventionCode") String conventionCode) {
    return ResponseEntity.ok(
        servicesService.getServices(
            limit,
            page,
            Service.Filter.builder()
                .conventionCode(conventionCode)
                .languageCode(languageCode)
                .onUs(onUs)
                .paymentTypeCode(paymentTypeCode)
                .pspCode(pspCode)
                .paymentMethodChannel(paymentMethodChannel)
                .brokerPspCode(brokerPspCode)
                .channelApp(channelApp)
                .channelCode(channelCode)
                .flagIo(flagIo)
                .flowId(flowId)
                .maximumAmount(maximumAmount)
                .minimumAmount(minimumAmount)
                .pspFlagStamp(pspFlagStamp)
                .build()));
  }
}
