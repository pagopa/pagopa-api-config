package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.*;

/** IbansEnhanced */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IbansEnhanced {

  @JsonProperty("ibans_enhanced")
  @Schema(required = true)
  @NotNull
  @Valid
  private List<IbanEnhanced> ibanEnhancedList;

  @Override
  public String toString(){
    StringBuilder builder = new StringBuilder();
    ibanEnhancedList.stream().limit(20).forEach(e -> builder.append(e.toString()));
    builder.append("[log truncated]...");
    return builder.toString();
  }
}
