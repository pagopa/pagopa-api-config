package it.gov.pagopa.apiconfig.core.model.massiveloading;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IbansMassLoadCsv {

    @Size(min=1)
    private List<IbanMassLoadCsv> ibanRows;

}
