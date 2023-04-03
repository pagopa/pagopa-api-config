package it.gov.pagopa.apiconfig.controller;

import static it.gov.pagopa.apiconfig.util.CommonUtil.getFilterAndOrder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.gov.pagopa.apiconfig.model.ProblemJson;
import it.gov.pagopa.apiconfig.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.model.psp.ChannelDetails;
import it.gov.pagopa.apiconfig.model.psp.ChannelPspList;
import it.gov.pagopa.apiconfig.model.psp.Channels;
import it.gov.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.service.ChannelsService;

@RestController()
@RequestMapping(path = "/channels")
@Tag(name = "Payment Service Providers", description = "Everything about Payment Service Providers")
@Validated
public class ChannelsController {

  @Autowired ChannelsService channelsService;

  @Operation(
      summary = "Get paginated list of channels",
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
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Channels.class))),
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
  public ResponseEntity<Channels> getChannels(
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
      @RequestParam(required = false, name = "ordering", defaultValue = "DESC")
          @Parameter(description = "Direction of ordering. Results are ordered by code")
          Sort.Direction ordering) {
    return ResponseEntity.ok(
        channelsService.getChannels(
            limit, page, getFilterAndOrder(filterByCode, null, Order.Channel.CODE, ordering)));
  }

  @Operation(
      summary = "Get Channel details ",
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
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChannelDetails.class))),
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
      value = "/{channelcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ChannelDetails> getChannel(
      @Size(max = 50)
          @Parameter(description = "channel code.", required = true)
          @PathVariable("channelcode")
          String channelCode) {
    return ResponseEntity.ok(channelsService.getChannel(channelCode));
  }

  @Operation(
      summary = "Create a Channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChannelDetails.class))),
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
  public ResponseEntity<ChannelDetails> createChannel(
      @RequestBody @Valid @NotNull ChannelDetails channelDetails) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelsService.createChannel(channelDetails));
  }

  @Operation(
      summary = "Update a Channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChannelDetails.class))),
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
      value = "/{channelcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ChannelDetails> updateChannel(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode,
      @RequestBody @Valid @NotNull ChannelDetails channelDetails) {
    return ResponseEntity.ok(channelsService.updateChannel(channelCode, channelDetails));
  }

  @Operation(
      summary = "Delete a Channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
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
      value = "/{channelcode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Void> deleteChannel(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode) {
    channelsService.deleteChannel(channelCode);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get a payment types of a channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PspChannelPaymentTypes.class))),
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
      value = "/{channelcode}/paymenttypes",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PspChannelPaymentTypes> getChannelPaymentTypes(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode) {
    return ResponseEntity.ok().body(channelsService.getPaymentTypes(channelCode));
  }

  @Operation(
      summary = "Create a payment types of a channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Created",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PspChannelPaymentTypes.class))),
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
      value = "/{channelcode}/paymenttypes",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PspChannelPaymentTypes> createChannelPaymentType(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode,
      @RequestBody PspChannelPaymentTypes pspChannelPaymentTypes) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(channelsService.createPaymentType(channelCode, pspChannelPaymentTypes));
  }

  @Operation(
      summary = "Delete a payment types of a channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
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
      value = "/{channelcode}/paymenttypes/{paymenttypecode}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<PspChannelPaymentTypes> deleteChannelPaymentType(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode,
      @PathVariable("paymenttypecode") String paymentTypeCode) {
    channelsService.deletePaymentType(channelCode, paymentTypeCode);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Get the list of PSPs associated with the channel",
      security = {
        @SecurityRequirement(name = "ApiKey"),
        @SecurityRequirement(name = "Authorization")
      },
      tags = {"Payment Service Providers"})
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content =
                @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChannelPspList.class))),
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
      value = "/{channelcode}/paymentserviceproviders",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<ChannelPspList> getChannelPaymentServiceProviders(
      @Positive
          @Parameter(description = "Number of elements on one page. Default = 50")
          @RequestParam(required = false, defaultValue = "50")
          Integer limit,
      @PositiveOrZero
          @Parameter(description = "Page number. Page value starts from 0", required = true)
          @RequestParam
          Integer page,
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCode,
      @RequestParam(required = false, name = "pspCode")
          @Parameter(description = "Filter by psp code")
          String filterByPspCode,
      @RequestParam(required = false, name = "pspName")
          @Parameter(description = "Filter by psp name")
          String filterByPspName,
      @RequestParam(required = false, name = "pspEnabled")
          @Parameter(description = "Filter by psp enabled")
          Boolean filterByPspEnabled) {
    return ResponseEntity.ok()
        .body(
            channelsService.getChannelPaymentServiceProviders(
                limit, page, channelCode, filterByPspCode, filterByPspName, filterByPspEnabled));
  }

  @Operation(
      summary = "Download the list of PSPs as CSV file",
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
      value = "/{channelcode}/paymentserviceproviders/csv",
      produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<Resource> getChannelPaymentServiceProvidersCSV(
      @Size(max = 50)
          @Parameter(description = "Channel code", required = true)
          @PathVariable("channelcode")
          String channelCod) {
    byte[] file = channelsService.getChannelPaymentServiceProvidersCSV(channelCod);
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(file.length)
        .body(new ByteArrayResource(file));
  }

  @Operation(
      summary = "Download the list of channelss as CSV file",
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
  public ResponseEntity<Resource> getChannelsCSV() {
    byte[] file = channelsService.getChannelsCSV();
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .contentLength(file.length)
        .body(new ByteArrayResource(file));
  }
}
