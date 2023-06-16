package it.gov.pagopa.apiconfig.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class WfespPluginConf extends WfespPluginConfBase {
  @JsonProperty("id_serv_plugin")
  @Schema(example = "idPsp1", required = true)
  @NotBlank
  @Size(max = 35)
  private String idServPlugin;
}
