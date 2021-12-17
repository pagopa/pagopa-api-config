package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class XSDValidation {

    @Schema(required = true, example = "false")
    private boolean xsdCompliant;
    @Schema(required = true, example = "https://raw.githubusercontent.com/pagopa/pagopa-api/master/general/InformativaContoAccredito_1_2_1.xsd")
    private String xsdSchema;
    @Schema(required = true, example = "Invalid content was found starting with element 'idBancaSeller'. One of '{ibanAccredito}' is expected. Error at lineNumber: 10")
    private String detail;
}
