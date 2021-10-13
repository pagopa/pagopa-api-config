package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

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
public class Broker {

    @JsonProperty("broker_code")
    @Schema(example = "223344556677889900", required = true)
    @Size(max = 35)
    @NotNull
    private String brokerCode;

    @JsonProperty("enabled")
    @Schema(required = true)
    @NotNull
    private Boolean enabled;

    @JsonProperty("description")
    @Schema(example = "Lorem ipsum dolor sit amet", required = true)
    @NotNull
    @Size(max = 255)
    private String description;

}
