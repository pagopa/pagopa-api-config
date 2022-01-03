package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Arrays;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    @JsonProperty("psp_code")
    @Size(max = 35)
    private String pspCode;

    @JsonProperty("flow_id")
    @Size(max = 35)
    private String flowId;

    @JsonProperty("psp_business_name")
    private String pspBusinessName;

    @JsonProperty("psp_flag_stamp")
    private Boolean pspFlagStamp;

    @JsonProperty("broker_psp_code")
    @Size(max = 35)
    private String brokerPspCode;

    @JsonProperty("channel_code")
    @Size(max = 35)
    private String channelCode;

    @JsonProperty("service_name")
    @Size(max = 35)
    private String serviceName;

    @JsonProperty("payment_method_channel")
    private Long paymentMethodChannel;

    @JsonProperty("payment_type_code")
    private PaymentTypeCode paymentTypeCode;

    @JsonProperty("language_code")
    @Size(max = 2)
    private LanguageCode languageCode;

    @JsonProperty("service_description")
    @Size(max = 511)
    private String serviceDescription;

    @JsonProperty("service_availability")
    @Size(max = 511)
    private String serviceAvailability;

    @JsonProperty("channel_url")
    private String channelUrl;

    @JsonProperty("minimum_amount")
    private Double minimumAmount;

    @JsonProperty("maximum_amount")
    private Double maximumAmount;

    @JsonProperty("fixed_cost")
    private Double fixedCost;

    @JsonProperty("timestamp_insertion")
    @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime timestampInsertion;

    @JsonProperty("validity_date")
    @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
    @JsonSerialize(using = OffsetDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime validityDate;

    @JsonProperty("logo_psp")
    private byte[] logoPsp;

    @JsonProperty("tags")
    @Size(max = 135)
    private String tags;

    @JsonProperty("logo_service")
    private byte[] logoService;

    @JsonProperty("channel_app")
    private Boolean channelApp;

    @JsonProperty("on_us")
    private Boolean onUs;

    @JsonProperty("cart_card")
    private Boolean cartCard;

    @JsonProperty("abi_code")
    @Size(max = 5)
    private String abiCode;

    @JsonProperty("mybank_code")
    @Size(max = 35)
    private String mybankCode;

    @JsonProperty("convention_code")
    @Size(max = 35)
    private String conventionCode;

    @JsonProperty("flag_io")
    private Boolean flagIo;

    @Getter
    public enum LanguageCode {
        IT("ITALIAN"),
        EN("ENGLISH"),
        FR("FRENCH"),
        DE("GERMAN"),
        SL("SLOVENE");

        private final String language;

        LanguageCode(String language) {
            this.language = language;
        }

    }

    @Getter
    public enum PaymentTypeCode {
        PAYPAL("PPAL"),
        POSTAL("BP"),
        TREASURY_BANK_TRANSFER("BBT"),
        DIRECT_DEBIT("AD"),
        PAYMENT_CARD("CP"),
        PSP_PAYMENT("PO"),
        ONLINE_BANKING_PAYMENT("OBEP"),
        JIFFY("JIF"),
        MYBANK("MYBK");

        private final String code;

        PaymentTypeCode(String code) {
            this.code = code;
        }

        public static PaymentTypeCode fromCode(String code) {
            return Arrays.stream(PaymentTypeCode.values())
                    .filter(elem -> elem.code.equals(code))
                    .findFirst()
                    .orElseThrow(() -> new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "PaymentTypeCode not found", "Cannot convert string '" + code + "' into enum"));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Filter {
        private String pspCode;
        private String brokerPspCode;
        private String channelCode;
        private Long paymentMethodChannel;
        private Service.PaymentTypeCode paymentTypeCode;
        private Boolean pspFlagStamp;
        private Boolean channelApp;
        private Boolean onUs;
        private Boolean flagIo;
        private String flowId;
        private Double minimumAmount;
        private Double maximumAmount;
        private Service.LanguageCode languageCode;
        private String conventionCode;
    }
}
