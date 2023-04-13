package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.creditorinstitution.Protocol;
import it.gov.pagopa.apiconfig.model.psp.ChannelDetails;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CanaliNodo;

public class ConvertChannelDetailsToCanali implements Converter<ChannelDetails, Canali> {

  @Override
  public Canali convert(MappingContext<ChannelDetails, Canali> context) {
    @Valid ChannelDetails source = context.getSource();
    return Canali.builder()
        .idCanale(source.getChannelCode())
        .enabled(source.getEnabled())
        .ip(source.getIp())
        .newPassword(source.getNewPassword())
        .password(source.getPassword())
        .porta(source.getPort())
        .protocollo(getProtocol(source.getProtocol()))
        .servizio(source.getService())
        .fkIntermediarioPsp(source.getFkIntermediarioPsp())
        .proxyEnabled(source.getProxyEnabled())
        .proxyHost(source.getProxyHost())
        .proxyPassword(source.getProxyPassword())
        .proxyPort(source.getProxyPort())
        .proxyUsername(source.getProxyUsername())
        .targetHost(source.getTargetHost())
        .targetPort(source.getTargetPort())
        .targetPath(source.getTargetPath())
        .targetHostNmp(source.getTargetHostNmp())
        .targetPortNmp(source.getTargetPortNmp())
        .targetPathNmp(source.getTargetPathNmp())
        .canaleNodo(true)
        .canaleAvv(false)
        .fkCanaliNodo(buildCanaliNodo(source))
        .timeout(120L)
        .numThread(source.getThreadNumber())
        .useNewFaultCode(source.getNewFaultCode())
        .timeoutA(source.getTimeoutA())
        .timeoutB(source.getTimeoutB())
        .timeoutC(source.getTimeoutC())
        .servizioNmp(source.getNmpService())
        .build();
  }

  private CanaliNodo buildCanaliNodo(ChannelDetails source) {
    return CanaliNodo.builder()
        .redirectIp(source.getRedirectIp())
        .redirectPath(source.getRedirectPath())
        .redirectPorta(source.getRedirectPort())
        .redirectQueryString(source.getRedirectQueryString())
        .redirectProtocollo(getProtocol(source.getRedirectProtocol()))
        .modelloPagamento(source.getPaymentModel().getDatabaseValue())
        .idServPlugin(source.getFkWfespPluginConf())
        .rtPush(source.getRtPush())
        .onUs(source.getOnUs())
        .carrelloCarte(source.getCardChart())
        .recovery(source.getRecovery())
        .marcaBolloDigitale(source.getDigitalStampBrand())
        .flagIo(source.getFlagIo())
        .multiPayment(true)
        .rptRtCompliant(true)
        .lento(false)
        .agidChannel(source.getAgid())
        .versionePrimitive(source.getPrimitiveVersion())
        .build();
  }

  private String getProtocol(Protocol source) {
    return source != null ? source.name() : null;
  }
}
