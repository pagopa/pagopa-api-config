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

    @CsvBindByName(required = true, column = "ragionesociale")
    private String ragioneSociale;

    @CsvBindByName(required = false, column = "descrizione")
    private String description;

    @CsvBindByName(required = true, column = "iban")
    private String iban;

    @CsvBindByName(required = true, column = "dataattivazioneiban")
    private String ibanActiveDate;

    @CsvBindByName(required = true, column = "operazione")
    private String operazione;

}
