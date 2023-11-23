package it.gov.pagopa.apiconfig.core.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.AbstractConverter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.massiveloading.Iban;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoad;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMaster;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;

public class ConvertIbansMassLoadToIbanMaster extends AbstractConverter<IbansMassLoad, IbansMaster> {
	
	@Override
	public IbansMaster convert(
		      MappingContext<IbansMassLoad, IbansMaster> context) {
		@Valid IbansMassLoad source = context.getSource();
		@Valid IbansMaster destination = context.getDestination();
		List<IbanMaster> ibanMasterList = destination.getIbanMasterList();

		for (Iban iban: source.getIbans()) {
			
			it.gov.pagopa.apiconfig.starter.entity.Iban ibanEntity =  it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
					.iban(iban.getIbanValue())
					.description(iban.getDescription())
					.fiscalCode(source.getCreditorInstitutionCode())
					.dueDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getDueDate()))))
					.build();


			IbanMaster ibanMaster = IbanMaster.builder()
					.description(source.getDescription())
					.validityDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(CommonUtil.ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getValidityDate()))))
					.insertedDate(Timestamp.from(Instant.now()))
					.iban(ibanEntity)
					.build();	  

			ibanMasterList.add(ibanMaster);

		}

		return destination;
		
	}
	
	@Override
	protected IbansMaster convert(IbansMassLoad source) {
		return new IbansMaster();
	}

}
