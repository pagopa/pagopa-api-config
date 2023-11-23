package it.gov.pagopa.apiconfig.core.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.gov.pagopa.apiconfig.core.mapper.ConvertBrokerDetailsToIntermediariPa;
import it.gov.pagopa.apiconfig.core.mapper.ConvertBrokerPspDetailsToIntermediariPsp;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCanaleTipoVersamentoToPaymentType;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCanaliToChannel;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCanaliToChannelDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCdiMasterToCdi;
import it.gov.pagopa.apiconfig.core.mapper.ConvertChannelDetailsToCanali;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCodifichePaToEncoding;
import it.gov.pagopa.apiconfig.core.mapper.ConvertConfigurationKeysToConfigurationKey;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCreditorInstitutionDetailsToPa;
import it.gov.pagopa.apiconfig.core.mapper.ConvertCreditorInstitutionStationPostToPaStazionePa;
import it.gov.pagopa.apiconfig.core.mapper.ConvertElencoServiziToService;
import it.gov.pagopa.apiconfig.core.mapper.ConvertEncodingToCodifichePa;
import it.gov.pagopa.apiconfig.core.mapper.ConvertFtpServersToFtpServer;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIbanAttributeMasterToIbanLabel;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIbanValidiPerPaToIban;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIbansMassLoadToIbanMaster;
import it.gov.pagopa.apiconfig.core.mapper.ConvertInformativeContoAccreditoMasterRepositoryToIca;
import it.gov.pagopa.apiconfig.core.mapper.ConvertInformativePaMasterToCounterpartTable;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIntermediariPaToBroker;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIntermediariPaToBrokerDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIntermediariPspToBrokerPsp;
import it.gov.pagopa.apiconfig.core.mapper.ConvertIntermediariPspToBrokerPspDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaStazionePaToCreditorInstitutionStation;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaStazionePaToCreditorInstitutionView;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaStazionePaToStationCreditorInstitution;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaToCreditorInstitution;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaToCreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaymentServiceProviderDetailsToPsp;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaymentTypeToString;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPaymentTypeToTipiVersamento;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPddEToPddM;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPddMToPddE;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPspCanaleTipoVersamentoToPaymentServiceProviderView;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPspCanaleTipoVersamentoToPspChannel;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPspToPaymentServiceProvider;
import it.gov.pagopa.apiconfig.core.mapper.ConvertPspToPaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertStationDetailsToStazioni;
import it.gov.pagopa.apiconfig.core.mapper.ConvertStazioniToStation;
import it.gov.pagopa.apiconfig.core.mapper.ConvertStazioniToStationDetails;
import it.gov.pagopa.apiconfig.core.mapper.ConvertTipiVersamentoToPaymentType;
import it.gov.pagopa.apiconfig.core.mapper.ConvertWfespPluginConfToWfespPluginConf;
import it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.core.model.configuration.FtpServer;
import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Broker;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.BrokerDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CounterpartTable;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStation;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionView;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ica;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMassLoad;
import it.gov.pagopa.apiconfig.core.model.massiveloading.IbansMaster;
import it.gov.pagopa.apiconfig.core.model.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.core.model.psp.BrokerPspDetails;
import it.gov.pagopa.apiconfig.core.model.psp.Cdi;
import it.gov.pagopa.apiconfig.core.model.psp.Channel;
import it.gov.pagopa.apiconfig.core.model.psp.ChannelDetails;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderView;
import it.gov.pagopa.apiconfig.core.model.psp.PspChannel;
import it.gov.pagopa.apiconfig.core.model.psp.Service;
import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.ElencoServizi;
import it.gov.pagopa.apiconfig.starter.entity.FtpServers;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
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
    Converter<WfespPluginConf, it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf>
        convertConfWfespPluginConf = new ConvertWfespPluginConfToWfespPluginConf();
    Converter<Pdd, it.gov.pagopa.apiconfig.core.model.configuration.Pdd> convertPddEToPddM =
        new ConvertPddEToPddM();
    Converter<it.gov.pagopa.apiconfig.core.model.configuration.Pdd, Pdd> convertPddMToPddE =
        new ConvertPddMToPddE();
    Converter<FtpServers, FtpServer> convertFtpServersFtpServer =
        new ConvertFtpServersToFtpServer();
    Converter<TipiVersamento, PaymentType> convertTipiVersamentoPaymentType =
        new ConvertTipiVersamentoToPaymentType();
    Converter<PaymentType, String> convertPaymentTypeString = new ConvertPaymentTypeToString();
    Converter<PaymentType, TipiVersamento> convertPaymentTypeTipiVersamento =
        new ConvertPaymentTypeToTipiVersamento();
    Converter<IbanAttributeMaster, IbanLabel> convertIbanAttributeMasterToIbanLabel =
        new ConvertIbanAttributeMasterToIbanLabel();

    ConvertPspCanaleTipoVersamentoToPaymentServiceProviderView
        convertPspCanaleTipoVersamentoToPaymentServiceProviderView =
            new ConvertPspCanaleTipoVersamentoToPaymentServiceProviderView();

    ConvertPaStazionePaToCreditorInstitutionView convertPaStazionePaToCreditorInstitutionView =
        new ConvertPaStazionePaToCreditorInstitutionView();
    
    Converter<IbansMassLoad, IbansMaster> convertIbansMassLoadToIbanMaster = new ConvertIbansMassLoadToIbanMaster();

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
            it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf.class)
        .setConverter(convertConfWfespPluginConf);
    mapper
        .createTypeMap(Pdd.class, it.gov.pagopa.apiconfig.core.model.configuration.Pdd.class)
        .setConverter(convertPddEToPddM);
    mapper
        .createTypeMap(it.gov.pagopa.apiconfig.core.model.configuration.Pdd.class, Pdd.class)
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
    mapper
        .createTypeMap(PspCanaleTipoVersamento.class, PaymentServiceProviderView.class)
        .setConverter(convertPspCanaleTipoVersamentoToPaymentServiceProviderView);
    mapper
        .createTypeMap(PaStazionePa.class, CreditorInstitutionView.class)
        .setConverter(convertPaStazionePaToCreditorInstitutionView);
    mapper
        .createTypeMap(IbanAttributeMaster.class, IbanLabel.class)
        .setConverter(convertIbanAttributeMasterToIbanLabel);
	
    mapper.createTypeMap(IbansMassLoad.class, IbansMaster.class)
			.setConverter(convertIbansMassLoadToIbanMaster);
    
    return mapper;
  }
}
