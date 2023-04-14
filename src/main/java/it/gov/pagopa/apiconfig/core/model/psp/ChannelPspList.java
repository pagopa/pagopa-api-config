package it.gov.pagopa.apiconfig.core.model.psp;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
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
