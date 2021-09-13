package it.pagopa.pagopa.apiconfig.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * CreditorIstitutionEncodings
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreditorIstitutionEncodings {
    @JsonProperty("id_dominio")
    @ApiModelProperty(example = "1234567890100", value = "")

    @Size(max = 35)
    private String idDominio;

    @JsonProperty("econdings")
    @ApiModelProperty(value = "")
    @Valid
    private List<Encoding> econdings = null;


}
