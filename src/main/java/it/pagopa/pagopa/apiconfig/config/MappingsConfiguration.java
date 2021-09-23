package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionFull;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionLight;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionFull;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionLight;
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

        Converter<Pa, CreditorInstitutionFull> convertPaToCreditorInstitutionFull = new ConvertPaToCreditorInstitutionFull();
        Converter<Pa, CreditorInstitutionLight> convertPaToCreditorInstitutionLight = new ConvertPaToCreditorInstitutionLight();
        Converter<Stazioni, Station> convertStazioniToStation = new ConvertStazioniToStation();

        mapper.createTypeMap(Pa.class, CreditorInstitutionFull.class).setConverter(convertPaToCreditorInstitutionFull);
        mapper.createTypeMap(Pa.class, CreditorInstitutionLight.class).setConverter(convertPaToCreditorInstitutionLight);
        mapper.createTypeMap(Stazioni.class, Station.class).setConverter(convertStazioniToStation);

        return mapper;
    }

}
