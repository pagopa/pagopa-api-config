package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.util.Constants;
import it.gov.pagopa.apiconfig.core.util.OffsetDateTimeDeserializer;
import java.time.OffsetDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

/** Iban (V2 Version) */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IbanEnhanced {

  @JsonProperty("iban")
  @Schema(example = "IT99C0222211111000000000000", required = true, description = "The iban code")
  @Pattern(regexp = "[a-zA-Z]{2,2}[0-9]{2,2}[a-zA-Z0-9]{1,30}", message = "IBAN code not valid")
  @Size(max = 35)
  @NotNull
  private String ibanValue;

  @JsonProperty(value = "ci_owner", access = JsonProperty.Access.READ_ONLY)
  @Schema(
      example = "77777777777",
      required = true,
      description = "Fiscal code of the Creditor Institution who owns the iban")
  @Size(max = 11)
  private String ciOwnerFiscalCode;

  @JsonProperty(value = "company_name", access = JsonProperty.Access.READ_ONLY)
  @Schema(example = "Comune di Firenze", description = "The Creditor Institution company name")
  @Size(max = 100)
  private String companyName;

  @JsonProperty("description")
  @Schema(
      example = "Riscossione Tributi",
      required = false,
      description = "The description the Creditor Institution gives to the iban about its usage")
  @Size(max = 300)
  private String description;

  @JsonProperty("is_active")
  @Schema(example = "true", required = true, description = "True if the iban is active")
  @NotNull
  private boolean isActive;

  @JsonProperty("validity_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @Schema(
      example = "2023-04-01T13:49:19.897Z",
      required = true,
      description = "The date the Creditor Institution wants the iban to be used for its payments")
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime validityDate;

  @JsonProperty(value = "publication_date", access = JsonProperty.Access.READ_ONLY)
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @Schema(
      example = "2023-06-01T23:59:59.999Z",
      required = true,
      description = "The date on which the iban has been inserted in the system")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime publicationDate;

  @JsonProperty(value = "due_date")
  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
  @JsonSerialize(using = OffsetDateTimeSerializer.class)
  @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
  @Schema(
      example = "2023-12-31T23:59:59.999Z",
      required = true,
      description = "The date on which the iban will expire")
  @NotNull
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime dueDate;

  @JsonProperty("labels")
  @Schema(description = "The labels array associated with the iban")
  private List<IbanLabel> labels;
}
