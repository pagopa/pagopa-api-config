package it.gov.pagopa.apiconfig.core.model.massiveloading;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CreditorInstitutionStation {

  @CsvBindByName(required = true, column = "cf")
  private String creditorInstitutionId;

  @CsvBindByName(required = true, column = "idstazione")
  private String stationId;

  @CsvBindByName(required = true, column = "ambiente")
  private Env environment;

  @CsvBindByName(required = true, column = "broadcast")
  private YesNo broadcast;

  @CsvBindByName(required = true, column = "auxdigit")
  private long auxDigit; // 0, 1, 2, 3

  @CsvBindByName(column = "codicesegregazione")
  private String segregationCode; // 2 cipher if auxDigit = 3, blank otherwise

  @CsvBindByName(column = "applicationcode")
  private String applicationCode; // 2 cipher if auxDigit = 3, blank otherwise

  @CsvBindByName(column = "datavalidita")
  private String validationDate;

  @CsvBindByName(column = "operazione")
  private Operation operation;

  @CsvBindByName(required = true, column = "aca")
  private Long aca; // 0 if false, 1 if true

  @CsvBindByName(required = true, column = "standIn")
  private Long standIn; // 0 if false, 1 if true

  // ESER: prod, COLL: dev|uat
  public enum Env {
    ESER,
    COLL;
  }

  // S: yes, N: no
  public enum YesNo {
    S,
    N;
  }

  // A: add relationship, C: remove relationship
  public enum Operation {
    A,
    C;
  }
}
