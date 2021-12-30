package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelDetails extends Channel {

    @ToString.Exclude
    @JsonProperty("password")
    @NotNull
    private String password;

    @ToString.Exclude
    @JsonProperty("new_password")
    private String newPassword;

    @JsonProperty("protocol")
    @NotNull
    private String protocol;

    @JsonProperty("ip")
    @NotNull
    private String ip;

    @JsonProperty("port")
    @NotNull
    private Long port;

    @JsonProperty("service")
    @NotNull
    private String service;

    @JsonProperty("broker_psp_code")
    @NotBlank
    private String brokerPspCode;

    @JsonProperty("proxy_enabled")
    private Boolean proxyEnabled;

    @JsonProperty("proxy_host")
    private String proxyHost;

    @JsonProperty("proxy_port")
    private Long proxyPort;

    @JsonProperty("proxy_username")
    private String proxyUsername;

    @ToString.Exclude
    @JsonProperty("proxy_password")
    private String proxyPassword;

    @JsonProperty("thread_number")
    @NotNull
    private Long threadNumber;

    @JsonProperty("timeout_a")
    @NotNull
    private Long timeoutA;

    @JsonProperty("timeout_b")
    @NotNull
    private Long timeoutB;

    @JsonProperty("timeout_c")
    @NotNull
    private Long timeoutC;

    @JsonProperty("npm_service")
    private String npmService;

    // CANALI_NODO

    @JsonProperty("redirect_ip")
    private String redirectIp;

    @JsonProperty("redirect_path")
    private String redirectPath;

    @JsonProperty("redirect_port")
    private Long redirectPort;

    @JsonProperty("redirect_query_string")
    private String redirectQueryString;

    @JsonProperty("redirect_protocol")
    private String redirectProtocol;

    @JsonProperty("payment_model")
    private PaymentModel paymentModel;

    @JsonProperty("serv_plugin")
    private String servPlugin;

    @JsonProperty("rt_push")
    private Boolean rtPush;

    @JsonProperty("on_us")
    private Boolean onUs;

    @JsonProperty("card_chart")
    private Boolean cardChart;

    @JsonProperty("recovery")
    private Boolean recovery;

    @JsonProperty("digital_stamp_brand")
    private Boolean digitalStampBrand;

    @JsonProperty("flag_io")
    private Boolean flagIo;


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
                    .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "PaymentModel not found", "Cannot convert string '" + value + "' into enum"));
        }

        public static ChannelDetails.PaymentModel fromDatabaseValue(String databaseValue) {
            return Arrays.stream(ChannelDetails.PaymentModel.values())
                    .filter(elem -> elem.databaseValue.equals(databaseValue))
                    .findFirst()
                    .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "PaymentModel not found", "Cannot convert string '" + databaseValue + "' into enum"));
        }
    }
}
