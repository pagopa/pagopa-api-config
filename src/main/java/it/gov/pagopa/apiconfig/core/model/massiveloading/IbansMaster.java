package it.gov.pagopa.apiconfig.core.model.massiveloading;

import java.util.ArrayList;
import java.util.List;

import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IbansMaster {
	@Builder.Default
	List<IbanMaster> ibanMasterList = new ArrayList<>();
}
