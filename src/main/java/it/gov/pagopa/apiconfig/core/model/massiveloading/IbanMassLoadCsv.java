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
public class IbanMassLoadCsv {

    @CsvBindByName(required = true, column = "iddominio")
    private String creditorInstitutionCode;

    @CsvBindByName(column = "descrizione")
    private String description;

    @CsvBindByName(required = true, column = "iban")
    private String iban;

    @CsvBindByName(column = "dataattivazioneiban")
    private String activationDate;

    @CsvBindByName(column = "datascadenzaiban")
    private String dueDate;

    @CsvBindByName(required = true, column = "operazione")
    private OperationEnum operation;

}
