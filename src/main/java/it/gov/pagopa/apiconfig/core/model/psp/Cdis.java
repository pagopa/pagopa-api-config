package it.gov.pagopa.apiconfig.core.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cdis {

  @JsonProperty("cdis")
  @Schema(required = true)
  @NotNull
  @Valid
  private List<Cdi> cdiList;

  @JsonProperty("page_info")
  @Schema(required = true)
  @NotNull
  @Valid
  private PageInfo pageInfo;
}
