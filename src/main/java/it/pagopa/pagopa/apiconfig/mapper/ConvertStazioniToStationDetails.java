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
                .enabled(source.getEnabled())
                .idStation(source.getIdStazione())
                .version(source.getVersione())
                .ip(source.getIp())
                .newPassword(source.getNewPassword())
                .password(source.getPassword())
                .port(source.getPorta())
                .redirectIp(source.getRedirectIp())
                .redirectPath(source.getRedirectPath())
                .redirectPort(source.getRedirectPorta())
                .redirectQueryString(source.getRedirectQueryString())
                .service(source.getServizio())
                .rtEnabled(source.getRtEnabled())
                .pofService(source.getServizioPof())
                .intermediaryId(source.getFkIntermediarioPa() != null ? source.getFkIntermediarioPa().getObjId() : null)
                .redirectProtocol(source.getRedirectProtocollo())
                .protocol4Mod(source.getIp4Mod())
                .ip4Mod(source.getIp4Mod())
                .port4Mod(source.getPorta4Mod())
                .service4Mod(source.getServizio4Mod())
                .proxyEnabled(source.getProxyEnabled())
                .proxyHost(source.getProxyHost())
                .proxyPort(source.getProxyPort())
                .proxyUsername(source.getProxyUsername())
                .proxyPassword(source.getProxyPassword())
                .protocolAvv(source.getProtocolloAvv())
                .threadNumber(source.getNumThread())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .flagOnline(source.getFlagOnline())
                .npmService(source.getServizioNmp())
                .build();
    }
}
