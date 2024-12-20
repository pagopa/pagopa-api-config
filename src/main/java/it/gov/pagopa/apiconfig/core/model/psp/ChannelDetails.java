package it.gov.pagopa.apiconfig.core.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Protocol;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import java.util.Arrays;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelDetails extends Channel {

  @JsonProperty("password")
  private String password;

  @JsonProperty("protocol")
  @NotNull
  private Protocol protocol;

  @JsonProperty("ip")
  private String ip;

  @Min(1)
  @Max(65535)
  @JsonProperty("port")
  @NotNull
  private Long port;

  @JsonProperty("service")
  private String service;

  @JsonProperty("broker_psp_code")
  @NotBlank
  private String brokerPspCode;

  @JsonProperty("proxy_enabled")
  private Boolean proxyEnabled;

  @JsonProperty("proxy_host")
  private String proxyHost;

  @Min(1)
  @Max(65535)
  @JsonProperty("proxy_port")
  private Long proxyPort;

  @JsonProperty("proxy_username")
  private String proxyUsername;

  @ToString.Exclude
  @JsonProperty("proxy_password")
  private String proxyPassword;

  @JsonProperty("target_host")
  private String targetHost;

  @JsonProperty("target_port")
  private Long targetPort;

  @JsonProperty("target_path")
  private String targetPath;

  @Min(1)
  @JsonProperty("thread_number")
  @NotNull
  private Long threadNumber;

  @Min(0)
  @JsonProperty("timeout_a")
  @NotNull
  @Builder.Default
  private Long timeoutA = 15L;

  @Min(0)
  @JsonProperty("timeout_b")
  @NotNull
  @Builder.Default
  private Long timeoutB = 30L;

  @Min(0)
  @JsonProperty("timeout_c")
  @NotNull
  @Builder.Default
  private Long timeoutC = 120L;

  @JsonProperty("nmp_service")
  private String nmpService;

  @JsonProperty("new_fault_code")
  private Boolean newFaultCode;

  @JsonProperty("target_host_nmp")
  private String targetHostNmp;

  @JsonProperty("target_port_nmp")
  private Long targetPortNmp;

  @JsonProperty("target_path_nmp")
  private String targetPathNmp;

  // CANALI_NODO

  @JsonProperty("redirect_ip")
  private String redirectIp;

  @JsonProperty("redirect_path")
  private String redirectPath;

  @Min(1)
  @Max(65535)
  @JsonProperty("redirect_port")
  private Long redirectPort;

  @JsonProperty("redirect_query_string")
  private String redirectQueryString;

  @JsonProperty("redirect_protocol")
  private Protocol redirectProtocol;

  @JsonProperty("payment_model")
  @Schema(required = true)
  @NotNull
  private PaymentModel paymentModel;

  @JsonProperty("serv_plugin")
  private String servPlugin;

  @JsonProperty("rt_push")
  @Schema(required = true)
  @NotNull
  private Boolean rtPush;

  @JsonProperty("on_us")
  @Schema(required = true)
  @NotNull
  private Boolean onUs;

  @JsonProperty("card_chart")
  @Schema(required = true)
  @NotNull
  private Boolean cardChart;

  @JsonProperty("recovery")
  @Schema(required = true)
  @NotNull
  private Boolean recovery;

  @JsonProperty("digital_stamp_brand")
  @Schema(required = true)
  @NotNull
  private Boolean digitalStampBrand;

  @JsonProperty("flag_io")
  private Boolean flagIo;

  @JsonProperty("agid")
  @Schema(required = true)
  @NotNull
  private Boolean agid;

  @JsonProperty("flag_psp_cp")
  @Schema(
      required = true,
      description =
          "Represents the authorization to carry out the transfer of the information present in"
              + " additional payment information in the tags relating to payment by card for the PA"
              + " in V1")
  @NotNull
  private Boolean flagPspCp;

  @JsonProperty("flag_standin")
  @Schema(
          required = true,
          description =
                  "Represents the authorization to use the standin mode with this channel")
  @NotNull
  private Boolean flagStandin;

  @JsonIgnore private IntermediariPsp fkIntermediarioPsp;

  @JsonIgnore private WfespPluginConf fkWfespPluginConf;

  @Getter
  public enum PaymentModel {
    IMMEDIATE("IMMEDIATE", "IMMEDIATO"),
    IMMEDIATE_MULTIBENEFICIARY("IMMEDIATE_MULTIBENEFICIARY", "IMMEDIATO_MULTIBENEFICIARIO"),
    DEFERRED("DEFERRED", "DIFFERITO"),
    ACTIVATED_AT_PSP("ACTIVATED_AT_PSP", "ATTIVATO_PRESSO_PSP");

    private final String value;
    private final String databaseValue;

    PaymentModel(String value, String databaseValue) {
      this.value = value;
      this.databaseValue = databaseValue;
    }

    public static ChannelDetails.PaymentModel fromValue(String value) {
      return Arrays.stream(ChannelDetails.PaymentModel.values())
          .filter(elem -> elem.value.equals(value))
          .findFirst()
          .orElseThrow(
              () ->
                  new AppException(
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      "PaymentModel not found",
                      "Cannot convert string '" + value + "' into enum"));
    }

    public static ChannelDetails.PaymentModel fromDatabaseValue(String databaseValue) {
      return Arrays.stream(ChannelDetails.PaymentModel.values())
          .filter(elem -> elem.databaseValue.equals(databaseValue))
          .findFirst()
          .orElseThrow(
              () ->
                  new AppException(
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      "PaymentModel not found",
                      "Cannot convert string '" + databaseValue + "' into enum"));
    }
  }
}
