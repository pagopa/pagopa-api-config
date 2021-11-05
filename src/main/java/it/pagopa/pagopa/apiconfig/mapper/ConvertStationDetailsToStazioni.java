package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertStationDetailsToStazioni implements Converter<StationDetails, Stazioni> {

    @Override
    public Stazioni convert(MappingContext<StationDetails, Stazioni> context) {
        @Valid StationDetails source = context.getSource();
        return Stazioni.builder()
                .idStazione(source.getStationCode())
                .enabled(source.getEnabled())
                .versione(source.getVersion())
                .newPassword(source.getNewPassword())
                .password(source.getPassword())
                .protocollo(source.getProtocol())
                .ip(source.getIp())
                .porta(source.getPort())
                .servizio(source.getService())
                .fkIntermediarioPa(IntermediariPa.builder()
                        .objId(source.getBrokerObjId())
                        .build())
                .protocollo4Mod(source.getProtocol4Mod())
                .ip4Mod(source.getIp4Mod())
                .porta4Mod(source.getPort4Mod())
                .servizio4Mod(source.getService4Mod())
                .redirectProtocollo(source.getRedirectProtocol())
                .redirectIp(source.getRedirectIp())
                .redirectPorta(source.getRedirectPort())
                .redirectPath(source.getRedirectPath())
                .redirectQueryString(source.getRedirectQueryString())
                .proxyEnabled(source.getProxyEnabled())
                .proxyHost(source.getProxyHost())
                .proxyPort(source.getProxyPort())
                .proxyUsername(source.getProxyUsername())
                .proxyPassword(source.getProxyPassword())
                .flagOnline(source.getFlagOnline())
                .numThread(source.getThreadNumber())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .rtEnabled(true)
                .build();
    }
}
