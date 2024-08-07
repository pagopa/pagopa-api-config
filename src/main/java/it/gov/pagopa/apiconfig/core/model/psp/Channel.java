package it.gov.pagopa.apiconfig.core.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel {

  @JsonProperty("channel_code")
  @Schema(example = "223344556677889900", required = true)
  @NotBlank
  private String channelCode;

  @JsonProperty("enabled")
  @Schema(required = true)
  @NotNull
  private Boolean enabled;

  @JsonProperty("broker_description")
  @Schema(
      description = "Broker description. Read only field",
      example = "Lorem ipsum dolor sit amet")
  private String brokerDescription;

  @Schema(required = true, description = "Primitive number version")
  @JsonProperty("primitive_version")
  private Integer primitiveVersion;
}
