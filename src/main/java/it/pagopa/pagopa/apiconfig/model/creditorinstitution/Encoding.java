package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import java.util.Arrays;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/** Encoding */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Encoding {

  @JsonProperty("code_type")
  @Schema(required = true, description = "BARCODE_GS1_128 is deprecated and not allowed")
  @NotNull
  private CodeTypeEnum codeType;

  @JsonProperty("encoding_code")
  @Schema(required = true, example = "0000111")
  @NotBlank
  private String encodingCode;

  @JsonIgnore private Long paObjId;

  @JsonIgnore private Long codificheObjId;

  /** Gets or Sets codeType */
  @Getter
  public enum CodeTypeEnum {
    QR_CODE("QR-CODE", false),
    BARCODE_128_AIM("BARCODE-128-AIM", false),

    BARCODE_GS1_128("BARCODE-GS1-128", true);

    private final String value;
    private final boolean deprecated;

    CodeTypeEnum(String value, boolean deprecated) {
      this.value = value;
      this.deprecated = deprecated;
    }

    public static CodeTypeEnum fromValue(String value) {
      return Arrays.stream(CodeTypeEnum.values())
          .filter(elem -> elem.value.equals(value))
          .findFirst()
          .orElseThrow(
              () ->
                  new AppException(
                      HttpStatus.INTERNAL_SERVER_ERROR,
                      "CodeTypeEnum not found",
                      "Cannot convert string '" + value + "' into enum"));
    }
  }
}
