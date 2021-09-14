package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Size;

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
    @JsonProperty("pa_code")
    @Schema(example = "000011050036", description = "")

    @Size(max = 35)
    private String paCode;
    @JsonProperty("code_type")
    @Schema(description = "")


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
