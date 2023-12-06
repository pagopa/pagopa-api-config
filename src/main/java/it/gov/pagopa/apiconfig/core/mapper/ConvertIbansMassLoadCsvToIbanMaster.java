package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.massiveloading.*;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import org.modelmapper.AbstractConverter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConvertIbansMassLoadCsvToIbanMaster extends AbstractConverter<IbansMassLoadCsv, IbansMaster> {
	
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
	
	@Override
	public IbansMaster convert(
		      MappingContext<IbansMassLoadCsv, IbansMaster> context) {
		@Valid IbansMassLoadCsv source = context.getSource();
		@Valid IbansMaster destination = context.getDestination();
		List<IbanMaster> ibanMasterList = destination.getIbanMasterList();

		for (IbanMassLoadCsv ibanRow: source.getIbanRows()) {
			
			it.gov.pagopa.apiconfig.starter.entity.Iban ibanEntity =  it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
					.iban(ibanRow.getIban())
					.description(ibanRow.getDescription())
					.fiscalCode(ibanRow.getCreditorInstitutionCode())
					.dueDate(Timestamp.valueOf(LocalDateTime.now().plusYears(1L)))
					.build();


			IbanMaster ibanMaster = IbanMaster.builder()
					.description(ibanRow.getDescription())
					.validityDate(Timestamp.valueOf(LocalDate.from(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN).parse(ibanRow.getIbanActiveDate())).atStartOfDay()))
					.insertedDate(Timestamp.from(Instant.now()))
					.iban(ibanEntity)
					.build();	  

			ibanMasterList.add(ibanMaster);

		}

		return destination;
		
	}

    @Override
	protected IbansMaster convert(IbansMassLoadCsv source) {
		return new IbansMaster();
	}

}
