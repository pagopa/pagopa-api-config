package it.gov.pagopa.apiconfig.core.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentServiceProviders {

  @JsonProperty("payment_service_providers")
  @Schema(required = true)
  @NotNull
  @Valid
  @Builder.Default
  private List<PaymentServiceProvider> paymentServiceProviderList = null;

  @JsonProperty("page_info")
  @Schema(required = true)
  @NotNull
  @Valid
  private PageInfo pageInfo;
}
