package it.gov.pagopa.apiconfig.core.model.configuration;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
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
@ToString
public class WfespPluginConfBase {

  @JsonProperty("pag_const_string_profile")
  @Schema(example = "Lorem ipsum dolor sit amet", required = true)
  @Size(max = 150)
  private String profiloPagConstString;

  @JsonProperty("pag_soap_rule_profile")
  @Schema(example = "IDVS=$buyerBank$", required = true)
  @Size(max = 150)
  private String profiloPagSoapRule;

  @JsonProperty("pag_rpt_xpath_profile")
  @Schema(example = "Lorem ipsum dolor sit amet", required = true)
  @Size(max = 150)
  private String profiloPagRptXpath;

  @JsonProperty("id_bean")
  @Schema(example = "defaultForwardProcessor", required = true)
  @Size(max = 255)
  private String idBean;
}
