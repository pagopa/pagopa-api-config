package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CounterpartTables {

    @JsonProperty("counterpart_tables")
    @Schema(required = true)
    @Valid
    private List<CounterpartTable> counterpartTableList;


    @JsonProperty("page_info")
    @Schema(required = true)
    @Valid
    private PageInfo pageInfo;

}
