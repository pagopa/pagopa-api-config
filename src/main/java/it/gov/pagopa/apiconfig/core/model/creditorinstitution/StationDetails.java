package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * StationDetails
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationDetails extends Station {

    @JsonProperty("ip")
    private String ip;

    @ToString.Exclude
    @JsonProperty("password")
    private String password;

    @Min(1)
    @Max(65535)
    @JsonProperty("port")
    @Schema(required = true)
    @NotNull
    private Long port;

    @JsonProperty("protocol")
    @Schema(required = true)
    @NotNull
    private Protocol protocol;

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

    @JsonProperty("service")
    private String service;

    @JsonProperty("pof_service")
    private String pofService;

    @JsonProperty("broker_code")
    @Schema(required = true)
    @NotBlank
    private String brokerCode;

    @JsonProperty("protocol_4mod")
    private Protocol protocol4Mod;

    @JsonProperty("ip_4mod")
    private String ip4Mod;

    @Min(1)
    @Max(65535)
    @JsonProperty("port_4mod")
    private Long port4Mod;

    @JsonProperty("service_4mod")
    private String service4Mod;

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

    @Min(1)
    @JsonProperty("thread_number")
    @NotNull
    private Long threadNumber;

    @Min(0)
    @JsonProperty("timeout_a")
    @Schema(required = true)
    @NotNull
    @Builder.Default
    private Long timeoutA = 15L;

    @Min(0)
    @JsonProperty("timeout_b")
    @Schema(required = true)
    @NotNull
    @Builder.Default
    private Long timeoutB = 30L;

    @Min(0)
    @JsonProperty("timeout_c")
    @Schema(required = true)
    @NotNull
    @Builder.Default
    private Long timeoutC = 120L;

    @JsonProperty("flag_online")
    private Boolean flagOnline;

    @JsonIgnore
    private Long brokerObjId;

    @JsonProperty("invio_rt_istantaneo")
    private Boolean rtInstantaneousDispatch;

    @JsonProperty("target_host")
    private String targetHost;

    @JsonProperty("target_port")
    private Long targetPort;

    @JsonProperty("target_path")
    private String targetPath;

    @JsonProperty("target_host_pof")
    private String targetHostPof;

    @JsonProperty("target_port_pof")
    private Long targetPortPof;

    @JsonProperty("target_path_pof")
    private String targetPathPof;

    @Min(1)
    @Max(2)
    @NotNull
    @Schema(required = true, description = "Primitive number version")
    @JsonProperty("primitive_version")
    private Integer primitiveVersion;

    @JsonProperty("flag_standin")
    @Schema(
            required = true,
            description =
                    "Represents the authorization to use the standin mode with this station")
    @NotNull
    private Boolean flagStandin;

    @JsonProperty("is_payment_options_enabled")
    private Boolean isPaymentOptionsEnabled = false;

    @JsonProperty("rest_endpoint")
    private String restEndpoint;
}
