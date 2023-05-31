package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import java.time.OffsetDateTime;
import java.util.List;

import javax.validation.Valid;
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

  @JsonProperty(value = "obj_id", access = JsonProperty.Access.READ_ONLY)
  @Schema(example = "1001",
          required = true,
          defaultValue = "",
          description = "The iban unique identifier")
  private long ibanId;

  @JsonProperty("iban")
  @Schema(example = "IT99C0222211111000000000000",
          required = true,
          description = "The iban code")
  @Size(max = 35)
  @NotNull
  private String ibanValue;

  @JsonProperty("validity_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(example = "2023-05-23",
          required = true,
          description = "The date the Creditor Institution wants the iban to be used for its payments")
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private OffsetDateTime validityDate;

  @JsonProperty(value = "publication_date", access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(example = "2023-05-23T10:38:07.165Z",
          required = true,
          description = "The date on which the iban has been inserted in the system")
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime publicationDate;

  @JsonProperty("isActive")
  @Schema(example = "true",
          required = false,
          defaultValue = "true",
          description = "Is the iban active?")
  private boolean isActive;

  @JsonProperty("description")
  @Schema(example = "Riscossione Tributi",
          required = false,
          defaultValue = "",
          description = "The description the Creditor Institution gives to the iban about its usage")
  @Size(max = 300)
  private String description;

  @JsonProperty("labels")
  @Schema(required = false,
          description = "The labels array associated with the iban")
  private List<IbanLabel> ibanLabels;

  @JsonProperty(value = "companyName", access = JsonProperty.Access.READ_ONLY)
  @Schema(example = "Comune di Firenze",
          required = true,
          defaultValue = "",
          description = "The Creditor Institution company name")
  @Size(max = 100)
  private String companyName;

  @JsonProperty(value = "ciOwner", access = JsonProperty.Access.READ_ONLY)
  @Schema(example = "01307110484",
          required = true,
          defaultValue = "",
          description = "Fiscal code of the Creditor Institution who owns the iban")
  @Size(max = 11)
  private String ciOwner;
}