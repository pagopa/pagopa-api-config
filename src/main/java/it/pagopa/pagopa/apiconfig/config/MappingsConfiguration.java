package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertBrokerDetailsToIntermediariPa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCodifichePaToEncoding;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCreditorInstitutionStationPostToPaStazionePa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertEncodingToCodifichePa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIbanValidiPerPaToIban;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativeContoAccreditoMasterRepositoryToIca;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativePaMasterToCounterpartTable;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBroker;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBrokerDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaStazionePaToCreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPspToPaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPspToPaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStationDetailsToStazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStationDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertToCreditorInstitutionDetailsToPa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Broker;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Iban;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Station;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
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
        Converter<PaStazionePa, CreditorInstitutionStation> convertPaStazionePaToCreditorInstitutionStation = new ConvertPaStazionePaToCreditorInstitutionStation();
        Converter<CodifichePa, Encoding> convertCodifichePaToEncoding = new ConvertCodifichePaToEncoding();
        Converter<IntermediariPa, Broker> convertIntermediariPaToBroker = new ConvertIntermediariPaToBroker();
        Converter<IbanValidiPerPa, Iban> convertIbanValidiPerPaToIban = new ConvertIbanValidiPerPaToIban();
        Converter<IntermediariPa, BrokerDetails> convertIntermediariPaToBrokerDetails = new ConvertIntermediariPaToBrokerDetails();
        Converter<InformativeContoAccreditoMaster, Ica> convertInformativeContoAccreditoMasterRepositoryToIca = new ConvertInformativeContoAccreditoMasterRepositoryToIca();
        Converter<InformativePaMaster, CounterpartTable> convertInformativePaMasterToCounterpartTable = new ConvertInformativePaMasterToCounterpartTable();
        Converter<CreditorInstitutionDetails, Pa> convertToCreditorInstitutionDetailsToPa = new ConvertToCreditorInstitutionDetailsToPa();
        Converter<BrokerDetails, IntermediariPa> convertBrokerDetailsToIntermediariPa = new ConvertBrokerDetailsToIntermediariPa();
        Converter<StationDetails, Stazioni> convertStationDetailsToStazioni = new ConvertStationDetailsToStazioni();
        Converter<Encoding, CodifichePa> convertEncodingToCodifichePa = new ConvertEncodingToCodifichePa();
        Converter<Psp, PaymentServiceProvider> convertPspToPaymentServiceProvider = new ConvertPspToPaymentServiceProvider();
        Converter<Psp, PaymentServiceProviderDetails> convertPspToPaymentServiceProviderDetails = new ConvertPspToPaymentServiceProviderDetails();
        Converter<CreditorInstitutionStationEdit, PaStazionePa> convertCreditorInstitutionStationPostToPaStazionePa = new ConvertCreditorInstitutionStationPostToPaStazionePa();

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
        mapper.createTypeMap(Encoding.class, CodifichePa.class).setConverter(convertEncodingToCodifichePa);
        mapper.createTypeMap(Psp.class, PaymentServiceProvider.class).setConverter(convertPspToPaymentServiceProvider);
        mapper.createTypeMap(Psp.class, PaymentServiceProviderDetails.class).setConverter(convertPspToPaymentServiceProviderDetails);
        mapper.createTypeMap(CreditorInstitutionStationEdit.class, PaStazionePa.class).setConverter(convertCreditorInstitutionStationPostToPaStazionePa);

        return mapper;
    }

}
