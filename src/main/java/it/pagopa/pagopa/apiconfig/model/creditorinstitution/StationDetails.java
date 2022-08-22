package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
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
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationDetails extends Station {

    @JsonProperty("ip")
    private String ip;

    @ToString.Exclude
    @JsonProperty("new_password")
    private String newPassword;

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
    private Long timeoutA = 15L;

    @Min(0)
    @JsonProperty("timeout_b")
    @Schema(required = true)
    @NotNull
    private Long timeoutB = 30L;

    @Min(0)
    @JsonProperty("timeout_c")
    @Schema(required = true)
    @NotNull
    private Long timeoutC = 120L;

    @JsonProperty("flag_online")
    private Boolean flagOnline;

    @JsonIgnore
    private Long brokerObjId;

    @JsonProperty("invio_rt_istantaneo")
    private Boolean rtInstantaneousDispatch;

}
