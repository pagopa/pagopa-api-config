package it.pagopa.pagopa.apiconfig.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WfespPluginConfs {
  @JsonProperty("wfesp_plugin_confs")
  @Schema(required = true)
  @NotNull
  @Valid
  private List<WfespPluginConf> wfespPluginConfList;
}
