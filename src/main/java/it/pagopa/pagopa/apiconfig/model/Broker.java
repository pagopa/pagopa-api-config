package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Broker
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Broker {

    @JsonProperty("organization_fiscal_code")
    @Schema(example = "1234567890100")
    @Size(max = 35)
    private String organizationFiscalCode;

    @JsonProperty("broker")
    @Schema()
    @Valid
    private List<BrokerDetails> broker = null;


}
