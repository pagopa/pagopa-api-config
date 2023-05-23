package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Iban */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Iban {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "obj_id")
  @Schema(example = "123456789", required = true)
  @NotBlank
  //@JsonIgnore
  private String objId;

  @JsonProperty("iban")
  @Schema(example = "IT99C0222211111000000000000", required = true)
  @Size(max = 35)
  private String ibanValue;

  @JsonProperty("validity_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(required = true)
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime validityDate;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY, value = "publication_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(required = true)
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime publicationDate;

  @JsonProperty("description")
  @Schema(example = "Riscossione Tributi", required = true)
  @Size(max = 300)
  private String description;

  @JsonProperty("isStandin")
  @Schema(example = "false",
          required = false,
          defaultValue = "false",
          allowableValues = {"true", "false"})
  @Size(max = 5)
  private String isStandin;

  @JsonProperty("isCup")
  @Schema(example = "false",
          required = false,
          defaultValue = "false",
          allowableValues = {"true", "false"})
  @Size(max = 5)
  private String isCup;
}
