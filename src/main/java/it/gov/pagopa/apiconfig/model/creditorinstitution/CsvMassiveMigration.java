package it.gov.pagopa.apiconfig.model.creditorinstitution;

import com.opencsv.bean.CsvBindByName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CsvMassiveMigration {

  @CsvBindByName(required = true, column = "cf")
  private String creditorInstitution;

  @CsvBindByName(required = true, column = "idstazione_provenienza")
  private String oldStation;

  @CsvBindByName(required = true, column = "idstazione_destinazione")
  private String newStation;

  @CsvBindByName(column = "broadcast")
  private YesNo broadcast;

  @Getter
  @AllArgsConstructor
  public enum YesNo {
    S(true),
    N(false);

    private final boolean value;
  }
}
