package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Encoding
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Encoding {

    @JsonProperty("code_type")
    @Schema(required = true)
    private CodeTypeEnum codeType;

    /**
     * Gets or Sets codeType
     */
    public enum CodeTypeEnum {
        BARCODE_GS1_128("BARCODE-GS1-128"),

        QR_CODE("QR-CODE"),

        BARCODE_128_AIM("BARCODE-128-AIM");

        private final String value;

        CodeTypeEnum(String value) {
            this.value = value;
        }

    }


}
