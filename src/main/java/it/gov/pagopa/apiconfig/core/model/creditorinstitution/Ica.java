package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.util.Constants;
import java.time.OffsetDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ica {
  @JsonProperty("id_ica")
  @Schema(example = "123456789", required = true)
  @NotBlank
  private String idIca;

  @JsonProperty("creditor_institution_code")
  @Schema(required = true, example = "1234567890100")
  @NotBlank
  private String creditorInstitutionCode;

  @JsonProperty("business_name")
  @Schema(required = true, example = "Comune di Lorem Ipsum")
  @NotBlank
  private String businessName;

  @JsonProperty("validity_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(required = true)
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime validityDate;

  @JsonProperty("publication_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(required = true)
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime publicationDate;
}
