package it.pagopa.pagopa.apiconfig.config;


import it.pagopa.pagopa.apiconfig.entity.*;
import it.pagopa.pagopa.apiconfig.mapper.*;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Broker;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Iban;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Station;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.model.psp.Channel;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannel;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
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
        Converter<CreditorInstitutionDetails, Pa> convertCreditorInstitutionDetailsToPa = new ConvertCreditorInstitutionDetailsToPa();
        Converter<BrokerDetails, IntermediariPa> convertBrokerDetailsToIntermediariPa = new ConvertBrokerDetailsToIntermediariPa();
        Converter<StationDetails, Stazioni> convertStationDetailsToStazioni = new ConvertStationDetailsToStazioni();
        Converter<Encoding, CodifichePa> convertEncodingToCodifichePa = new ConvertEncodingToCodifichePa();
        Converter<Psp, PaymentServiceProvider> convertPspToPaymentServiceProvider = new ConvertPspToPaymentServiceProvider();
        Converter<Psp, PaymentServiceProviderDetails> convertPspToPaymentServiceProviderDetails = new ConvertPspToPaymentServiceProviderDetails();
        Converter<CreditorInstitutionStationEdit, PaStazionePa> convertCreditorInstitutionStationPostToPaStazionePa = new ConvertCreditorInstitutionStationPostToPaStazionePa();
        Converter<IntermediariPsp, BrokerPsp> convertIntermediariPspToBrokerPsp = new ConvertIntermediariPspToBrokerPsp();
        Converter<IntermediariPsp, BrokerPspDetails> convertIntermediariPspToBrokerPspDetails = new ConvertIntermediariPspToBrokerPspDetails();
        Converter<Canali, Channel> convertCanaliToChannel = new ConvertCanaliToChannel();
        Converter<Canali, ChannelDetails> convertCanaliToChannelDetails = new ConvertCanaliToChannelDetails();
        Converter<ElencoServizi, Service> convertElencoServiziToService = new ConvertElencoServiziToService();
        Converter<CdiMaster, Cdi> convertCdiMasterToCdi = new ConvertCdiMasterToCdi();
        Converter<PspCanaleTipoVersamento, PspChannel> convertPspCanaleTipoVersamentoToPspChannel = new ConvertPspCanaleTipoVersamentoToPspChannel();
        Converter<BrokerPspDetails, IntermediariPsp> convertBrokerPspDetailsToIntermediariPsp = new ConvertBrokerPspDetailsToIntermediariPsp();
        Converter<PaymentServiceProviderDetails, Psp> convertPaymentServiceProviderDetailsToPsp = new ConvertPaymentServiceProviderDetailsToPsp();
        Converter<ChannelDetails, Canali> convertChannelDetailsToCanali = new ConvertChannelDetailsToCanali();
        Converter<ConfigurationKeys, ConfigurationKey> convertConfigurationKeysConfigurationKey = new ConvertConfigurationKeysToConfigurationKey();

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
        mapper.createTypeMap(CreditorInstitutionDetails.class, Pa.class).setConverter(convertCreditorInstitutionDetailsToPa);
        mapper.createTypeMap(BrokerDetails.class, IntermediariPa.class).setConverter(convertBrokerDetailsToIntermediariPa);
        mapper.createTypeMap(StationDetails.class, Stazioni.class).setConverter(convertStationDetailsToStazioni);
        mapper.createTypeMap(Encoding.class, CodifichePa.class).setConverter(convertEncodingToCodifichePa);
        mapper.createTypeMap(Psp.class, PaymentServiceProvider.class).setConverter(convertPspToPaymentServiceProvider);
        mapper.createTypeMap(Psp.class, PaymentServiceProviderDetails.class).setConverter(convertPspToPaymentServiceProviderDetails);
        mapper.createTypeMap(CreditorInstitutionStationEdit.class, PaStazionePa.class).setConverter(convertCreditorInstitutionStationPostToPaStazionePa);
        mapper.createTypeMap(IntermediariPsp.class, BrokerPsp.class).setConverter(convertIntermediariPspToBrokerPsp);
        mapper.createTypeMap(IntermediariPsp.class, BrokerPspDetails.class).setConverter(convertIntermediariPspToBrokerPspDetails);
        mapper.createTypeMap(Canali.class, Channel.class).setConverter(convertCanaliToChannel);
        mapper.createTypeMap(Canali.class, ChannelDetails.class).setConverter(convertCanaliToChannelDetails);
        mapper.createTypeMap(ElencoServizi.class, Service.class).setConverter(convertElencoServiziToService);
        mapper.createTypeMap(CdiMaster.class, Cdi.class).setConverter(convertCdiMasterToCdi);
        mapper.createTypeMap(PspCanaleTipoVersamento.class, PspChannel.class).setConverter(convertPspCanaleTipoVersamentoToPspChannel);
        mapper.createTypeMap(BrokerPspDetails.class, IntermediariPsp.class).setConverter(convertBrokerPspDetailsToIntermediariPsp);
        mapper.createTypeMap(PaymentServiceProviderDetails.class, Psp.class).setConverter(convertPaymentServiceProviderDetailsToPsp);
        mapper.createTypeMap(ChannelDetails.class, Canali.class).setConverter(convertChannelDetailsToCanali);
        mapper.createTypeMap(ConfigurationKeys.class, ConfigurationKey.class).setConverter(convertConfigurationKeysConfigurationKey);

        return mapper;
    }

}
