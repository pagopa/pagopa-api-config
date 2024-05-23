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
public class CbillMassiveLoadCsv {

    @CsvBindByName(required = true, column = "CF")
    private String creditorInstitutionCode;

    @CsvBindByName(required = true, column = "SIA")
    private String cbillCode;

    @CsvBindByName(required = true, column = "RAGIONESOCIALE")
    private String creditorCompanyName;
}
