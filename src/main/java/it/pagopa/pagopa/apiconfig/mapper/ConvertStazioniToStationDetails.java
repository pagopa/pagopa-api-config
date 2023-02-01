package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Protocol;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
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
                .newPassword(CommonUtil.deNull(source.getNewPassword()))
                .password(CommonUtil.deNull(source.getPassword()))
                .protocol(Protocol.fromValue(source.getProtocollo()))
                .ip(CommonUtil.deNull(source.getIp()))
                .port(source.getPorta())
                .service(CommonUtil.deNull(source.getServizio()))
                .pofService(CommonUtil.deNull(source.getServizioPof()))
                .protocol4Mod(source.getProtocollo4Mod() != null ? Protocol.fromValue(source.getProtocollo4Mod()) : null)
                .brokerCode(CommonUtil.deNull(source.getIntermediarioPa().getIdIntermediarioPa()))
                .ip4Mod(CommonUtil.deNull(source.getIp4Mod()))
                .port4Mod(source.getPorta4Mod())
                .service4Mod(CommonUtil.deNull(source.getServizio4Mod()))
                .redirectProtocol(source.getRedirectProtocollo() != null ? Protocol.fromValue(source.getRedirectProtocollo()) : null)
                .redirectIp(CommonUtil.deNull(source.getRedirectIp()))
                .redirectPort(source.getRedirectPorta())
                .redirectPath(CommonUtil.deNull(source.getRedirectPath()))
                .redirectQueryString(CommonUtil.deNull(source.getRedirectQueryString()))
                .proxyEnabled(CommonUtil.deNull(source.getProxyEnabled()))
                .proxyHost(CommonUtil.deNull(source.getProxyHost()))
                .proxyPort(source.getProxyPort())
                .proxyUsername(CommonUtil.deNull(source.getProxyUsername()))
                .proxyPassword(CommonUtil.deNull(source.getProxyPassword()))
                .targetHost(CommonUtil.deNull(source.getTargetHost()))
                .targetPort(source.getTargetPort())
                .targetPath(CommonUtil.deNull(source.getTargetPath()))
                .flagOnline(CommonUtil.deNull(source.getFlagOnline()))
                .threadNumber(source.getNumThread())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .rtInstantaneousDispatch(source.getInvioRtIstantaneo())
                .primitiveVersion(source.getVersionePrimitive())
                .build();
    }
}
