package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.Station;
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

        Converter<Pa, CreditorInstitution> convertPaToCreditorInstitution = new ConvertPaToCreditorInstitution();
        Converter<Stazioni, Station> convertStazioniToStation = new ConvertStazioniToStation();

        mapper.createTypeMap(Pa.class, CreditorInstitution.class).setConverter(convertPaToCreditorInstitution);
        mapper.createTypeMap(Stazioni.class, Station.class).setConverter(convertStazioniToStation);

        return mapper;
    }

}
