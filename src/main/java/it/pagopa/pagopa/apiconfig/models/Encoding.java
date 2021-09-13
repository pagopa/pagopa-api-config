package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
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
public class Encoding {
    @JsonProperty("pa_code")
    @ApiModelProperty(example = "000011050036", value = "")

    @Size(max = 35)
    private String paCode;
    @JsonProperty("code_type")
    @ApiModelProperty(value = "")


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
