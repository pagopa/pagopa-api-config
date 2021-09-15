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
 * CreditorInstitutionEncodings
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionEncodings {
    @JsonProperty("id_dominio")
    @Schema(example = "1234567890100")
    @Size(max = 35)
    private String idDominio;

    @JsonProperty("encodings")
    @Schema()
    @Valid
    private List<Encoding> encodings = null;


}
