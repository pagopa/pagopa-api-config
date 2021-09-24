package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Stations
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationCI extends StationDetails {

    @JsonProperty("notice_number")
    private Long noticeNumber;

    @JsonProperty("aux_digit")
    private Long auxDigit;

    @JsonProperty("segregation_number")
    private Long segregationNumber;

    @JsonProperty("fourth_model")
    private String fourthModel;

    @JsonProperty("broadcast")
    private String broadcast;

}
