package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.pagopa.apiconfig.model.PageInfo;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelPspList {

  @JsonProperty("payment_service_providers")
  @Schema(required = true)
  @NotNull
  private List<ChannelPsp> psp;

  @JsonProperty("page_info")
  @Schema(required = true)
  @NotNull
  @Valid
  private PageInfo pageInfo;
}
