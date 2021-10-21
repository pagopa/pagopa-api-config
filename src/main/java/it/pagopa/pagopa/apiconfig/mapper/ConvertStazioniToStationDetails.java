package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertStazioniToStationDetails implements Converter<Stazioni, StationDetails> {

    @Override
    public StationDetails convert(MappingContext<Stazioni, StationDetails> context) {
        @Valid Stazioni source = context.getSource();
        return StationDetails.builder()
                .stationCode(source.getIdStazione())
                .enabled(source.getEnabled())
                .version(source.getVersione())
                .newPassword(source.getNewPassword())
                .password(source.getPassword())
                .protocol(source.getProtocollo())
                .ip(source.getIp())
                .port(source.getPorta())
                .service(source.getServizio())
                .protocol4Mod(source.getProtocollo4Mod())
                .brokerCode(source.getFkIntermediarioPa() != null ? source.getFkIntermediarioPa().getIdIntermediarioPa() : null)
                .ip4Mod(source.getIp4Mod())
                .port4Mod(source.getPorta4Mod())
                .service4Mod(source.getServizio4Mod())
                .redirectProtocol(source.getRedirectProtocollo())
                .redirectIp(source.getRedirectIp())
                .redirectPort(source.getRedirectPorta())
                .redirectPath(source.getRedirectPath())
                .redirectQueryString(source.getRedirectQueryString())
                .proxyEnabled(source.getProxyEnabled())
                .proxyHost(source.getProxyHost())
                .proxyPort(source.getProxyPort())
                .proxyUsername(source.getProxyUsername())
                .proxyPassword(source.getProxyPassword())
                .flagOnline(source.getFlagOnline())
                .threadNumber(source.getNumThread())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .build();
    }
}
