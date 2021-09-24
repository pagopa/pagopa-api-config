package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * BrokerDetails
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Broker {

    @JsonProperty("broker_code")
    @Schema(example = "223344556677889900", required = true)
    @Size(max = 35)
    private String idBroker;

    @JsonProperty("enabled")
    @Schema(required = true)
    private Boolean enabled;

    @JsonProperty("description")
    @Schema(example = "Lorem ipsum dolor sit amet", required = true)
    @NotNull
    @Size(max = 255)
    private String description;

    @JsonProperty("extended_fault_bean")
    @Schema(required = true)
    @Valid
    private Boolean extendedFaultBean;

    @JsonProperty("creditor_institution_code")
    @Schema(example = "1234567890100")
    @Size(max = 35)
    private String creditorInstitutionCode;

}
