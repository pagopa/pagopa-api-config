package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Protocol;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.isConnectionSync;

public class ConvertStazioniToStationDetails implements Converter<Stazioni, StationDetails> {

    @Override
    public StationDetails convert(MappingContext<Stazioni, StationDetails> context) {
        @Valid Stazioni source = context.getSource();
        return StationDetails.builder()
                .stationCode(source.getIdStazione())
                .enabled(source.getEnabled())
                .version(source.getVersione())
                .password(CommonUtil.deNull(source.getPassword()))
                .protocol(Protocol.fromValue(source.getProtocollo()))
                .ip(CommonUtil.deNull(source.getIp()))
                .port(source.getPorta())
                .service(CommonUtil.deNull(source.getServizio()))
                .pofService(CommonUtil.deNull(source.getServizioPof()))
                .protocol4Mod(
                        source.getProtocollo4Mod() != null
                                ? Protocol.fromValue(source.getProtocollo4Mod())
                                : null)
                .brokerCode(CommonUtil.deNull(source.getIntermediarioPa().getIdIntermediarioPa()))
                .ip4Mod(CommonUtil.deNull(source.getIp4Mod()))
                .port4Mod(source.getPorta4Mod())
                .service4Mod(CommonUtil.deNull(source.getServizio4Mod()))
                .redirectProtocol(
                        source.getRedirectProtocollo() != null
                                ? Protocol.fromValue(source.getRedirectProtocollo())
                                : null)
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
                .targetHostPof(CommonUtil.deNull(source.getTargetHostPof()))
                .targetPortPof(source.getTargetPortPof())
                .targetPathPof(CommonUtil.deNull(source.getTargetPathPof()))
                .flagOnline(CommonUtil.deNull(source.getFlagOnline()))
                .threadNumber(source.getNumThread())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .rtInstantaneousDispatch(source.getInvioRtIstantaneo())
                .primitiveVersion(source.getVersionePrimitive())
                .isConnectionSync(isConnectionSync(source))
                .verifyPaymentOptionEnabled(source.getVerifyPaymentOptionEnabled())
                .verifyPaymentOptionEndpoint(source.getVerifyPaymentOptionEndpoint())
                .build();
    }
}
