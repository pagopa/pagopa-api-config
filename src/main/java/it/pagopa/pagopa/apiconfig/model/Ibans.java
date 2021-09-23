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
 * Ibans
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ibans {
    @JsonProperty("organization_fiscal_code")
    @Schema(example = "1234567890100")
    @Size(max = 35)
    private String organizationFiscalCode;

    @JsonProperty("ibans")
    @Schema()
    @Valid
    private List<Iban> ibans = null;


}
