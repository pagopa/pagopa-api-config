package it.gov.pagopa.apiconfig.core.mapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.validation.Valid;

import org.modelmapper.AbstractConverter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionView;
import it.gov.pagopa.apiconfig.core.model.massiveloading.Iban;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoad;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;

public class ConvertIbansMassLoadToIbanMaster extends AbstractConverter<IbansMassLoad, ArrayList<IbanMaster>> {
	
	private static final String ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
/*
	@Override
	public ArrayList<IbanMaster> convert(IbansMassLoad source) {
		ArrayList<IbanMaster> ibanMasterList = new ArrayList<>();

		for (Iban iban: source.getIbans()) {
			
			it.gov.pagopa.apiconfig.starter.entity.Iban ibanEntity =  it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
					.iban(iban.getIbanValue())
					.description(iban.getDescription())
					.fiscalCode(source.getCreditorInstitutionCode())
					.dueDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getDueDate()))))
					.build();


			IbanMaster ibanMaster = IbanMaster.builder()
					.description(source.getDescription())
					.validityDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getValidityDate()))))
					.insertedDate(Timestamp.from(Instant.now()))
					.iban(ibanEntity)
					.build();	  

			ibanMasterList.add(ibanMaster);

		}

		return ibanMasterList;
	}*/
	
	@Override
	public ArrayList<IbanMaster> convert(
		      MappingContext<IbansMassLoad, ArrayList<IbanMaster>> context) {
		@Valid IbansMassLoad source = context.getSource();
		ArrayList<IbanMaster> ibanMasterList = context.getDestination();

		for (Iban iban: source.getIbans()) {
			
			it.gov.pagopa.apiconfig.starter.entity.Iban ibanEntity =  it.gov.pagopa.apiconfig.starter.entity.Iban.builder()
					.iban(iban.getIbanValue())
					.description(iban.getDescription())
					.fiscalCode(source.getCreditorInstitutionCode())
					.dueDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getDueDate()))))
					.build();


			IbanMaster ibanMaster = IbanMaster.builder()
					.description(source.getDescription())
					.validityDate(Timestamp.valueOf(LocalDateTime.from(DateTimeFormatter.ofPattern(ISO_DATE_FORMAT_ZERO_OFFSET).parse(iban.getValidityDate()))))
					.insertedDate(Timestamp.from(Instant.now()))
					.iban(ibanEntity)
					.build();	  

			ibanMasterList.add(ibanMaster);

		}

		return ibanMasterList;
		
	}

	@Override
	protected ArrayList<IbanMaster> convert(IbansMassLoad source) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
