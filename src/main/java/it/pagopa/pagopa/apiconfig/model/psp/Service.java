package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import it.pagopa.pagopa.apiconfig.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

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
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum PaymentTypeCode {
        BBT("Bonifico Bancario di Tesoreria"),
        BP("Bollettino Postale"),
        AD("Addebito Diretto"),
        CP("Carta di Pagamento"),
        PO("Pagamento attivato presso PSP"),
        OBEP("Online Banking Electronic Payment"),
        JIF("Bancomat Pay"),
        MYBK("MyBank Seller Bank"),
        PPAL("PayPal");

        private final String description;


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
