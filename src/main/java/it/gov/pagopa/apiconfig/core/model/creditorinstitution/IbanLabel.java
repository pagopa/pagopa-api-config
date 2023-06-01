package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Size;
import lombok.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IbanLabel {

    @JsonProperty("name")
    @Schema(example = "CUP",
            required = true,
            description = "The label")
    @Size(max = 50)
    private Labels name;

    @JsonProperty("description")
    @Schema(example = "The iban to use for CUP payments",
            required = true,
            description = "A short description about the label")
    @Size(max = 200)
    private String description;

    @Getter
    @AllArgsConstructor
    public enum Labels {
        CUP("Canone Unico"),
        ACA("standin / archivio centralizzato archivi");

        /** The field name identify the column */
        private final String description;
    }
}
