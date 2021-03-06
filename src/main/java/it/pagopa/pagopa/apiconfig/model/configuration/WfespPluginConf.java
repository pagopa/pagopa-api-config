package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WfespPluginConf extends WfespPluginConfBase {
    @JsonProperty("id_serv_plugin")
    @Schema(example = "idPsp1", required = true)
    @NotBlank
    @Size(max = 35)
    private String idServPlugin;
}
