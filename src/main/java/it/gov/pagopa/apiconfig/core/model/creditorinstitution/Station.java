package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Station */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Station {
  @JsonProperty("station_code")
  @Schema(example = "1234567890100", required = true)
  @NotBlank
  @Size(max = 35)
  private String stationCode;

  @JsonProperty("enabled")
  @Schema(required = true, description = "station enabled", defaultValue = "true")
  @NotNull
  private Boolean enabled;

  @JsonProperty("broker_description")
  @Schema(
      description = "Broker description. Read only field",
      example = "Lorem ipsum dolor sit amet")
  private String brokerDescription;

  @Min(1)
  @Max(2)
  @JsonProperty("version")
  @Schema(required = true, description = "number version")
  @NotNull
  private Long version;

  @Schema(description = "Describe the station connection's type, true synchronous, false asynchronous")
  @JsonProperty("is_connection_sync")
  private Boolean isConnectionSync;
}
