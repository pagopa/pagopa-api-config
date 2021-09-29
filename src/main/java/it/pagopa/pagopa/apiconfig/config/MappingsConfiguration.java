package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIbanValidiPerPaToIban;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaStazionePaToStationCI;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStationDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.Iban;
import it.pagopa.pagopa.apiconfig.model.Station;
import it.pagopa.pagopa.apiconfig.model.StationCI;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingsConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<Pa, CreditorInstitutionDetails> convertPaToCreditorInstitutionDetails = new ConvertPaToCreditorInstitutionDetails();
        Converter<Pa, CreditorInstitution> convertPaToCreditorInstitution = new ConvertPaToCreditorInstitution();
        Converter<Stazioni, Station> convertStazioniToStation = new ConvertStazioniToStation();
        Converter<Stazioni, StationDetails> convertStazioniToStationDetails = new ConvertStazioniToStationDetails();
        Converter<PaStazionePa, StationCI> convertPaStazionePaToStationCI = new ConvertPaStazionePaToStationCI();
        Converter<IbanValidiPerPa, Iban> convertIbanValidiPerPaToIban = new ConvertIbanValidiPerPaToIban();

        mapper.createTypeMap(Pa.class, CreditorInstitutionDetails.class).setConverter(convertPaToCreditorInstitutionDetails);
        mapper.createTypeMap(Pa.class, CreditorInstitution.class).setConverter(convertPaToCreditorInstitution);
        mapper.createTypeMap(Stazioni.class, Station.class).setConverter(convertStazioniToStation);
        mapper.createTypeMap(Stazioni.class, StationDetails.class).setConverter(convertStazioniToStationDetails);
        mapper.createTypeMap(PaStazionePa.class, StationCI.class).setConverter(convertPaStazionePaToStationCI);
        mapper.createTypeMap(IbanValidiPerPa.class, Iban.class).setConverter(convertIbanValidiPerPaToIban);

        return mapper;
    }

}
