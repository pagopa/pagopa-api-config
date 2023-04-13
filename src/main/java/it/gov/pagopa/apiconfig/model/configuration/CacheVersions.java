package it.gov.pagopa.apiconfig.model.configuration;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.apiconfig.model.PageInfo;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CacheVersions {
  @JsonProperty("version_list")
  @Schema(required = true)
  @NotNull
  @Valid
  private List<Cache> versionList;

  @JsonProperty("page_info")
  @Schema(required = true)
  @NotNull
  @Valid
  private PageInfo pageInfo;
}
