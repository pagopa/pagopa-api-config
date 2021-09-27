package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * StationDetails
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationDetails extends Station {

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("password")
    private String password;

    @JsonProperty("port")
    private Long port;

    @JsonProperty("redirect_ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    private String redirectPath;

    @JsonProperty("redirect_port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    private String redirectQueryString;

    @JsonProperty("service")
    private String service;

    @JsonProperty("rt_enabled")
    private Boolean rtEnabled;

    @JsonProperty("pof_service")
    private String pofService;

    @JsonProperty("intermediary_id")
    private Long intermediaryId;

    @JsonProperty("redirect_protocol")
    private String redirectProtocol;

    @JsonProperty("protocol_4mod")
    private String protocol4Mod;

    @JsonProperty("ip_4mod")
    private String ip4Mod;

    @JsonProperty("port_4mod")
    private Long port4Mod;

    @JsonProperty("service_4mod")
    private String service4Mod;

    @JsonProperty("proxy_enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    private String proxyHost;

    @JsonProperty("proxy_port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    private String proxyUsername;

    @JsonProperty("proxy_password")
    private String proxyPassword;

    @JsonProperty("protocol_avv")
    private String protocolAvv;

    @JsonProperty("thread_number")
    private Long threadNumber;

    @JsonProperty("timeout_a")
    private Long timeoutA;

    @JsonProperty("timeout_b")
    private Long timeoutB;

    @JsonProperty("timeout_c")
    private Long timeoutC;

    @JsonProperty("flag_online")
    private Boolean flagOnline;

    @JsonProperty("npm_service")
    private String npmService;


}
