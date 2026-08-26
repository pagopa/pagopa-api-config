package it.gov.pagopa.apiconfig.core.model.cds;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CdsSoggettoServizioRequestDto {

  @Schema(description = "Subject service identifier")
  private String id;

  @Schema(description = "Subject identifier")
  private String idSoggetto;

  @Schema(description = "Start validity date")
  private ZonedDateTime dataInizioValidita;

  @Schema(description = "End validity date")
  private ZonedDateTime dataFineValidita;

  @Schema(description = "Commission flag")
  private Boolean commissione;

  @Schema(description = "Service identifier")
  private String idServizio;

  @JsonProperty("descrizione_servizio")
  @Schema(description = "Service description")
  private String descrizioneServizio;

  @Schema(description = "Station identifier")
  private String idStazione;
}
