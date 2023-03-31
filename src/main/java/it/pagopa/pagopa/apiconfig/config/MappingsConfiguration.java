package it.pagopa.pagopa.apiconfig.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.ElencoServizi;
import it.gov.pagopa.apiconfig.starter.entity.FtpServers;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.InformativeContoAccreditoMaster;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Pdd;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.mapper.ConvertBrokerDetailsToIntermediariPa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertBrokerPspDetailsToIntermediariPsp;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCanaleTipoVersamentoToPaymentType;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCanaliToChannel;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCanaliToChannelDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCdiMasterToCdi;
import it.pagopa.pagopa.apiconfig.mapper.ConvertChannelDetailsToCanali;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCodifichePaToEncoding;
import it.pagopa.pagopa.apiconfig.mapper.ConvertConfigurationKeysToConfigurationKey;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCreditorInstitutionDetailsToPa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertCreditorInstitutionStationPostToPaStazionePa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertElencoServiziToService;
import it.pagopa.pagopa.apiconfig.mapper.ConvertEncodingToCodifichePa;
import it.pagopa.pagopa.apiconfig.mapper.ConvertFtpServersToFtpServer;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIbanValidiPerPaToIban;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativeContoAccreditoMasterRepositoryToIca;
import it.pagopa.pagopa.apiconfig.mapper.ConvertInformativePaMasterToCounterpartTable;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBroker;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPaToBrokerDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPspToBrokerPsp;
import it.pagopa.pagopa.apiconfig.mapper.ConvertIntermediariPspToBrokerPspDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaStazionePaToCreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaStazionePaToStationCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitution;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaToCreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaymentServiceProviderDetailsToPsp;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaymentTypeToString;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPaymentTypeToTipiVersamento;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPddEToPddM;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPddMToPddE;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPspCanaleTipoVersamentoToPspChannel;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPspToPaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.mapper.ConvertPspToPaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStationDetailsToStazioni;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStation;
import it.pagopa.pagopa.apiconfig.mapper.ConvertStazioniToStationDetails;
import it.pagopa.pagopa.apiconfig.mapper.ConvertTipiVersamentoToPaymentType;
import it.pagopa.pagopa.apiconfig.mapper.ConvertWfespPluginConfToWfespPluginConf;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServer;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
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
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationCreditorInstitution;
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

@Configuration
public class MappingsConfiguration {

  @Bean
  ModelMapper modelMapper() {

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    Converter<Pa, CreditorInstitutionDetails> convertPaToCreditorInstitutionDetails =
        new ConvertPaToCreditorInstitutionDetails();
    Converter<Pa, CreditorInstitution> convertPaToCreditorInstitution =
        new ConvertPaToCreditorInstitution();
    Converter<Stazioni, Station> convertStazioniToStation = new ConvertStazioniToStation();
    Converter<Stazioni, StationDetails> convertStazioniToStationDetails =
        new ConvertStazioniToStationDetails();
    Converter<PaStazionePa, CreditorInstitutionStation>
        convertPaStazionePaToCreditorInstitutionStation =
            new ConvertPaStazionePaToCreditorInstitutionStation();
    Converter<PaStazionePa, StationCreditorInstitution>
        convertPaStazionePaToStationCreditorInstitution =
            new ConvertPaStazionePaToStationCreditorInstitution();
    Converter<CodifichePa, Encoding> convertCodifichePaToEncoding =
        new ConvertCodifichePaToEncoding();
    Converter<IntermediariPa, Broker> convertIntermediariPaToBroker =
        new ConvertIntermediariPaToBroker();
    Converter<IbanValidiPerPa, Iban> convertIbanValidiPerPaToIban =
        new ConvertIbanValidiPerPaToIban();
    Converter<IntermediariPa, BrokerDetails> convertIntermediariPaToBrokerDetails =
        new ConvertIntermediariPaToBrokerDetails();
    Converter<InformativeContoAccreditoMaster, Ica>
        convertInformativeContoAccreditoMasterRepositoryToIca =
            new ConvertInformativeContoAccreditoMasterRepositoryToIca();
    Converter<InformativePaMaster, CounterpartTable> convertInformativePaMasterToCounterpartTable =
        new ConvertInformativePaMasterToCounterpartTable();
    Converter<CreditorInstitutionDetails, Pa> convertCreditorInstitutionDetailsToPa =
        new ConvertCreditorInstitutionDetailsToPa();
    Converter<BrokerDetails, IntermediariPa> convertBrokerDetailsToIntermediariPa =
        new ConvertBrokerDetailsToIntermediariPa();
    Converter<StationDetails, Stazioni> convertStationDetailsToStazioni =
        new ConvertStationDetailsToStazioni();
    Converter<Encoding, CodifichePa> convertEncodingToCodifichePa =
        new ConvertEncodingToCodifichePa();
    Converter<Psp, PaymentServiceProvider> convertPspToPaymentServiceProvider =
        new ConvertPspToPaymentServiceProvider();
    Converter<Psp, PaymentServiceProviderDetails> convertPspToPaymentServiceProviderDetails =
        new ConvertPspToPaymentServiceProviderDetails();
    Converter<CreditorInstitutionStationEdit, PaStazionePa>
        convertCreditorInstitutionStationPostToPaStazionePa =
            new ConvertCreditorInstitutionStationPostToPaStazionePa();
    Converter<IntermediariPsp, BrokerPsp> convertIntermediariPspToBrokerPsp =
        new ConvertIntermediariPspToBrokerPsp();
    Converter<IntermediariPsp, BrokerPspDetails> convertIntermediariPspToBrokerPspDetails =
        new ConvertIntermediariPspToBrokerPspDetails();
    Converter<Canali, Channel> convertCanaliToChannel = new ConvertCanaliToChannel();
    Converter<Canali, ChannelDetails> convertCanaliToChannelDetails =
        new ConvertCanaliToChannelDetails();
    Converter<ElencoServizi, Service> convertElencoServiziToService =
        new ConvertElencoServiziToService();
    Converter<CdiMaster, Cdi> convertCdiMasterToCdi = new ConvertCdiMasterToCdi();
    Converter<PspCanaleTipoVersamento, PspChannel> convertPspCanaleTipoVersamentoToPspChannel =
        new ConvertPspCanaleTipoVersamentoToPspChannel();
    Converter<BrokerPspDetails, IntermediariPsp> convertBrokerPspDetailsToIntermediariPsp =
        new ConvertBrokerPspDetailsToIntermediariPsp();
    Converter<PaymentServiceProviderDetails, Psp> convertPaymentServiceProviderDetailsToPsp =
        new ConvertPaymentServiceProviderDetailsToPsp();
    Converter<ChannelDetails, Canali> convertChannelDetailsToCanali =
        new ConvertChannelDetailsToCanali();
    Converter<ConfigurationKeys, ConfigurationKey> convertConfigurationKeysConfigurationKey =
        new ConvertConfigurationKeysToConfigurationKey();
    Converter<CanaleTipoVersamento, PaymentType> convertCanaleTipoVersamentoToPaymentType =
        new ConvertCanaleTipoVersamentoToPaymentType();
    Converter<WfespPluginConf, it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf>
        convertConfWfespPluginConf = new ConvertWfespPluginConfToWfespPluginConf();
    Converter<Pdd, it.pagopa.pagopa.apiconfig.model.configuration.Pdd> convertPddEToPddM =
        new ConvertPddEToPddM();
    Converter<it.pagopa.pagopa.apiconfig.model.configuration.Pdd, Pdd> convertPddMToPddE =
        new ConvertPddMToPddE();
    Converter<FtpServers, FtpServer> convertFtpServersFtpServer =
        new ConvertFtpServersToFtpServer();
    Converter<TipiVersamento, PaymentType> convertTipiVersamentoPaymentType =
        new ConvertTipiVersamentoToPaymentType();
    Converter<PaymentType, String> convertPaymentTypeString = new ConvertPaymentTypeToString();
    Converter<PaymentType, TipiVersamento> convertPaymentTypeTipiVersamento =
        new ConvertPaymentTypeToTipiVersamento();

    mapper
        .createTypeMap(Pa.class, CreditorInstitutionDetails.class)
        .setConverter(convertPaToCreditorInstitutionDetails);
    mapper
        .createTypeMap(Pa.class, CreditorInstitution.class)
        .setConverter(convertPaToCreditorInstitution);
    mapper.createTypeMap(Stazioni.class, Station.class).setConverter(convertStazioniToStation);
    mapper
        .createTypeMap(Stazioni.class, StationDetails.class)
        .setConverter(convertStazioniToStationDetails);
    mapper
        .createTypeMap(PaStazionePa.class, CreditorInstitutionStation.class)
        .setConverter(convertPaStazionePaToCreditorInstitutionStation);
    mapper
        .createTypeMap(PaStazionePa.class, StationCreditorInstitution.class)
        .setConverter(convertPaStazionePaToStationCreditorInstitution);
    mapper
        .createTypeMap(CodifichePa.class, Encoding.class)
        .setConverter(convertCodifichePaToEncoding);
    mapper
        .createTypeMap(IntermediariPa.class, Broker.class)
        .setConverter(convertIntermediariPaToBroker);
    mapper
        .createTypeMap(IbanValidiPerPa.class, Iban.class)
        .setConverter(convertIbanValidiPerPaToIban);
    mapper
        .createTypeMap(IntermediariPa.class, BrokerDetails.class)
        .setConverter(convertIntermediariPaToBrokerDetails);
    mapper
        .createTypeMap(InformativeContoAccreditoMaster.class, Ica.class)
        .setConverter(convertInformativeContoAccreditoMasterRepositoryToIca);
    mapper
        .createTypeMap(InformativePaMaster.class, CounterpartTable.class)
        .setConverter(convertInformativePaMasterToCounterpartTable);
    mapper
        .createTypeMap(CreditorInstitutionDetails.class, Pa.class)
        .setConverter(convertCreditorInstitutionDetailsToPa);
    mapper
        .createTypeMap(BrokerDetails.class, IntermediariPa.class)
        .setConverter(convertBrokerDetailsToIntermediariPa);
    mapper
        .createTypeMap(StationDetails.class, Stazioni.class)
        .setConverter(convertStationDetailsToStazioni);
    mapper
        .createTypeMap(Encoding.class, CodifichePa.class)
        .setConverter(convertEncodingToCodifichePa);
    mapper
        .createTypeMap(Psp.class, PaymentServiceProvider.class)
        .setConverter(convertPspToPaymentServiceProvider);
    mapper
        .createTypeMap(Psp.class, PaymentServiceProviderDetails.class)
        .setConverter(convertPspToPaymentServiceProviderDetails);
    mapper
        .createTypeMap(CreditorInstitutionStationEdit.class, PaStazionePa.class)
        .setConverter(convertCreditorInstitutionStationPostToPaStazionePa);
    mapper
        .createTypeMap(IntermediariPsp.class, BrokerPsp.class)
        .setConverter(convertIntermediariPspToBrokerPsp);
    mapper
        .createTypeMap(IntermediariPsp.class, BrokerPspDetails.class)
        .setConverter(convertIntermediariPspToBrokerPspDetails);
    mapper.createTypeMap(Canali.class, Channel.class).setConverter(convertCanaliToChannel);
    mapper
        .createTypeMap(Canali.class, ChannelDetails.class)
        .setConverter(convertCanaliToChannelDetails);
    mapper
        .createTypeMap(ElencoServizi.class, Service.class)
        .setConverter(convertElencoServiziToService);
    mapper.createTypeMap(CdiMaster.class, Cdi.class).setConverter(convertCdiMasterToCdi);
    mapper
        .createTypeMap(PspCanaleTipoVersamento.class, PspChannel.class)
        .setConverter(convertPspCanaleTipoVersamentoToPspChannel);
    mapper
        .createTypeMap(BrokerPspDetails.class, IntermediariPsp.class)
        .setConverter(convertBrokerPspDetailsToIntermediariPsp);
    mapper
        .createTypeMap(PaymentServiceProviderDetails.class, Psp.class)
        .setConverter(convertPaymentServiceProviderDetailsToPsp);
    mapper
        .createTypeMap(ChannelDetails.class, Canali.class)
        .setConverter(convertChannelDetailsToCanali);
    mapper
        .createTypeMap(ConfigurationKeys.class, ConfigurationKey.class)
        .setConverter(convertConfigurationKeysConfigurationKey);
    mapper
        .createTypeMap(CanaleTipoVersamento.class, PaymentType.class)
        .setConverter(convertCanaleTipoVersamentoToPaymentType);
    mapper
        .createTypeMap(
            WfespPluginConf.class,
            it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf.class)
        .setConverter(convertConfWfespPluginConf);
    mapper
        .createTypeMap(Pdd.class, it.pagopa.pagopa.apiconfig.model.configuration.Pdd.class)
        .setConverter(convertPddEToPddM);
    mapper
        .createTypeMap(it.pagopa.pagopa.apiconfig.model.configuration.Pdd.class, Pdd.class)
        .setConverter(convertPddMToPddE);
    mapper
        .createTypeMap(FtpServers.class, FtpServer.class)
        .setConverter(convertFtpServersFtpServer);
    mapper
        .createTypeMap(TipiVersamento.class, PaymentType.class)
        .setConverter(convertTipiVersamentoPaymentType);
    mapper.createTypeMap(PaymentType.class, String.class).setConverter(convertPaymentTypeString);
    mapper
        .createTypeMap(PaymentType.class, TipiVersamento.class)
        .setConverter(convertPaymentTypeTipiVersamento);

    return mapper;
  }
}
