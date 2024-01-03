package it.gov.pagopa.apiconfig.core.model.massiveloading;

import java.util.List;

import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IbansMassLoadCsv {

    @Size(min=1)
    private List<IbanMassLoadCsv> ibanRows;

}
