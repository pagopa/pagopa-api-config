package it.pagopa.pagopa.apiconfig.model.configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** PDD */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PddBase {

  @JsonProperty("enabled")
  @Schema(example = "yes", required = true)
  @NotNull
  private Boolean enabled;

  @JsonProperty("description")
  @Schema(example = "Lorem ipsum dolor sit amet", required = true)
  @NotNull
  private String description;

  @JsonProperty("ip")
  @Schema(example = "localhost", required = true)
  @NotNull
  private String ip;

  @Min(1)
  @Max(65535)
  @JsonProperty("port")
  @Schema(example = "1234", required = false)
  private Integer port;
}
