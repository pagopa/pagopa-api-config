package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * PDD
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pdd {

    @JsonProperty("id_pdd")
    @Schema(example = "localhost", required = true)
    @NotBlank
    private String idPdd;

    @JsonProperty("enabled")
    @Schema(example = "yes", required = true)
    @NotNull
    private Boolean enabled;

    @JsonProperty("description")
    @Schema(example = "Lorem ipsum dolor sit amet", required = true)
    @NotNull
    private String description;

    @JsonProperty("ip")
    @Schema(example = "locahost", required = true)
    @NotNull
    private String ip;

    @JsonProperty("port")
    @Schema(example = "1234", required = false)
    private Integer port;

}
