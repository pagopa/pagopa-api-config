package it.pagopa.pagopa.apiconfig.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageInfo {

    @JsonProperty("page")
    @Schema(description = "Page number", required = true)
    @PositiveOrZero
    Integer page;

    @JsonProperty("limit")
    @Schema(description = "Required number of items per page", required = true)
    @Positive
    Integer limit;

    @JsonProperty("items_found")
    @Schema(description = "Number of items found. (The last page may have fewer elements than required)", required = true)
    @PositiveOrZero
    Integer itemsFound;

    @JsonProperty("total_pages")
    @Schema(description = "Total number of pages", required = true)
    @PositiveOrZero
    Integer totalPages;
}
