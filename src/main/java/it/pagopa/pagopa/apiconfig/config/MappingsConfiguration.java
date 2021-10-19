package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertBrokerDetailsToIntermediariPa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCodifichePaToEncoding;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIbanValidiPerPaToIban;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativeContoAccreditoMasterRepositoryToIca;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativePaMasterToCounterpartTable;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBroker;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBrokerDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaStazionePaToCreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStationDetailsToStazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStationDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertToCreditorInstitutionDetailsToPa;
import it.pagopa.pagopa.apiconfig.model.Broker;
import it.pagopa.pagopa.apiconfig.model.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import it.pagopa.pagopa.apiconfig.model.Iban;
import it.pagopa.pagopa.apiconfig.model.Ica;
import it.pagopa.pagopa.apiconfig.model.Station;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MappingsConfiguration {


    @Bean
    @Autowired
    public ModelMapper modelMapper(ConvertStazioniToStation convertStazioniToStation,
                                   ConvertStationDetailsToStazioni convertStationDetailsToStazioni,
                                   ConvertPaToCreditorInstitutionDetails convertPaToCreditorInstitutionDetails,
                                   ConvertPaToCreditorInstitution convertPaToCreditorInstitution,
                                   ConvertBrokerDetailsToIntermediariPa convertBrokerDetailsToIntermediariPa,
                                   ConvertStazioniToStationDetails convertStazioniToStationDetails,
                                   ConvertPaStazionePaToCreditorInstitutionStation convertPaStazionePaToCreditorInstitutionStation,
                                   ConvertCodifichePaToEncoding convertCodifichePaToEncoding,
                                   ConvertToCreditorInstitutionDetailsToPa convertToCreditorInstitutionDetailsToPa,
                                   ConvertIntermediariPaToBroker convertIntermediariPaToBroker,
                                   ConvertIbanValidiPerPaToIban convertIbanValidiPerPaToIban,
                                   ConvertIntermediariPaToBrokerDetails convertIntermediariPaToBrokerDetails,
                                   ConvertInformativePaMasterToCounterpartTable convertInformativePaMasterToCounterpartTable,
                                   ConvertInformativeContoAccreditoMasterRepositoryToIca convertInformativeContoAccreditoMasterRepositoryToIca) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        mapper.createTypeMap(Pa.class, CreditorInstitutionDetails.class).setConverter(convertPaToCreditorInstitutionDetails);
        mapper.createTypeMap(Pa.class, CreditorInstitution.class).setConverter(convertPaToCreditorInstitution);
        mapper.createTypeMap(Stazioni.class, Station.class).setConverter(convertStazioniToStation);
        mapper.createTypeMap(Stazioni.class, StationDetails.class).setConverter(convertStazioniToStationDetails);
        mapper.createTypeMap(PaStazionePa.class, CreditorInstitutionStation.class).setConverter(convertPaStazionePaToCreditorInstitutionStation);
        mapper.createTypeMap(CodifichePa.class, Encoding.class).setConverter(convertCodifichePaToEncoding);
        mapper.createTypeMap(IntermediariPa.class, Broker.class).setConverter(convertIntermediariPaToBroker);
        mapper.createTypeMap(IbanValidiPerPa.class, Iban.class).setConverter(convertIbanValidiPerPaToIban);
        mapper.createTypeMap(IntermediariPa.class, BrokerDetails.class).setConverter(convertIntermediariPaToBrokerDetails);
        mapper.createTypeMap(InformativeContoAccreditoMaster.class, Ica.class).setConverter(convertInformativeContoAccreditoMasterRepositoryToIca);
        mapper.createTypeMap(InformativePaMaster.class, CounterpartTable.class).setConverter(convertInformativePaMasterToCounterpartTable);
        mapper.createTypeMap(CreditorInstitutionDetails.class, Pa.class).setConverter(convertToCreditorInstitutionDetailsToPa);
        mapper.createTypeMap(BrokerDetails.class, IntermediariPa.class).setConverter(convertBrokerDetailsToIntermediariPa);
        mapper.createTypeMap(StationDetails.class, Stazioni.class).setConverter(convertStationDetailsToStazioni);

        return mapper;
    }

}
