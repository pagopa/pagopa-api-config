package it.pagopa.pagopa.apiconfig.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * BrokerDetails
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrokerPsp {

    @JsonProperty("broker_psp_code")
    @Schema(example = "223344556677889900", required = true)
    @NotBlank
    @Size(max = 35)
    private String brokerPspCode;

    @JsonProperty("description")
    @Schema(required = true)
    @NotNull
    private String description;

    @JsonProperty("enabled")
    @Schema(required = true)
    @NotNull
    private Boolean enabled;

}
