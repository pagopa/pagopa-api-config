package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Protocol;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationDetails;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertStationDetailsToStazioni implements Converter<StationDetails, Stazioni> {

  @Override
  public Stazioni convert(MappingContext<StationDetails, Stazioni> context) {
    @Valid StationDetails source = context.getSource();
    return Stazioni.builder()
        .idStazione(source.getStationCode())
        .enabled(source.getEnabled())
        .versione(source.getVersion())
        .password(source.getPassword())
        .protocollo(getProtocol(source.getProtocol()))
        .ip(source.getIp())
        .porta(source.getPort())
        .servizio(source.getService())
        .servizioPof(source.getPofService())
        .intermediarioPa(IntermediariPa.builder().objId(source.getBrokerObjId()).build())
        .protocollo4Mod(getProtocol(source.getProtocol4Mod()))
        .ip4Mod(source.getIp4Mod())
        .porta4Mod(source.getPort4Mod())
        .servizio4Mod(source.getService4Mod())
        .redirectProtocollo(getProtocol(source.getRedirectProtocol()))
        .redirectIp(source.getRedirectIp())
        .redirectPorta(source.getRedirectPort())
        .redirectPath(source.getRedirectPath())
        .redirectQueryString(source.getRedirectQueryString())
        .proxyEnabled(source.getProxyEnabled())
        .proxyHost(source.getProxyHost())
        .proxyPort(source.getProxyPort())
        .proxyUsername(source.getProxyUsername())
        .proxyPassword(source.getProxyPassword())
        .targetHost(source.getTargetHost())
        .targetPort(source.getTargetPort())
        .targetPath(source.getTargetPath())
        .targetHostPof(source.getTargetHostPof())
        .targetPortPof(source.getTargetPortPof())
        .targetPathPof(source.getTargetPathPof())
        .flagOnline(source.getFlagOnline())
        .numThread(source.getThreadNumber())
        .timeoutA(source.getTimeoutA())
        .timeoutB(source.getTimeoutB())
        .timeoutC(source.getTimeoutC())
        .rtEnabled(true)
        .invioRtIstantaneo(source.getRtInstantaneousDispatch())
        .versionePrimitive(source.getPrimitiveVersion())
        .dataCreazione(CommonUtil.toTimestamp(source.getCreateDate()))
        .build();
  }

  private String getProtocol(Protocol source) {
    return source != null ? source.name() : null;
  }
}
