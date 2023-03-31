package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Protocol;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;

public class ConvertCanaliToChannelDetails implements Converter<Canali, ChannelDetails> {

  @Override
  public ChannelDetails convert(MappingContext<Canali, ChannelDetails> context) {
    @Valid Canali source = context.getSource();
    ChannelDetails.ChannelDetailsBuilder<?, ?> builder =
        ChannelDetails.builder()
            .channelCode(source.getIdCanale())
            .enabled(source.getEnabled())
            .brokerDescription(source.getFkIntermediarioPsp().getCodiceIntermediario())
            .ip(source.getIp())
            .newPassword(source.getNewPassword())
            .password(source.getPassword())
            .port(source.getPorta())
            .protocol(Protocol.fromValue(source.getProtocollo()))
            .service(source.getServizio())
            .brokerPspCode(source.getFkIntermediarioPsp().getIdIntermediarioPsp())
            .proxyEnabled(source.getProxyEnabled())
            .proxyHost(source.getProxyHost())
            .proxyPort(source.getProxyPort())
            .proxyUsername(source.getProxyUsername())
            .proxyPassword(source.getProxyPassword())
            .targetHost(source.getTargetHost())
            .targetPort(source.getTargetPort())
            .targetPath(source.getTargetPath())
            .targetHostNmp(source.getTargetHostNmp())
            .targetPortNmp(source.getTargetPortNmp())
            .targetPathNmp(source.getTargetPathNmp())
            .threadNumber(source.getNumThread())
            .timeoutA(source.getTimeoutA())
            .timeoutB(source.getTimeoutB())
            .timeoutC(source.getTimeoutC())
            .newFaultCode(source.getUseNewFaultCode())
            .nmpService(source.getServizioNmp());
    if (source.getFkCanaliNodo() != null) {
      builder
          .redirectIp(source.getFkCanaliNodo().getRedirectIp())
          .redirectPath(source.getFkCanaliNodo().getRedirectPath())
          .redirectPort(source.getFkCanaliNodo().getRedirectPorta())
          .redirectQueryString(source.getFkCanaliNodo().getRedirectQueryString())
          .redirectProtocol(
              source.getFkCanaliNodo().getRedirectProtocollo() != null
                  ? Protocol.fromValue(source.getFkCanaliNodo().getRedirectProtocollo())
                  : null)
          .paymentModel(getPaymentModel(source.getFkCanaliNodo().getModelloPagamento()))
          .rtPush(source.getFkCanaliNodo().getRtPush())
          .onUs(source.getFkCanaliNodo().getOnUs())
          .cardChart(source.getFkCanaliNodo().getCarrelloCarte())
          .recovery(source.getFkCanaliNodo().getRecovery())
          .digitalStampBrand(source.getFkCanaliNodo().getMarcaBolloDigitale())
          .agid(source.getFkCanaliNodo().getAgidChannel())
          .primitiveVersion(source.getFkCanaliNodo().getVersionePrimitive())
          .flagIo(source.getFkCanaliNodo().getFlagIo());
      if (source.getFkCanaliNodo().getIdServPlugin() != null) {
        builder.servPlugin(source.getFkCanaliNodo().getIdServPlugin().getIdServPlugin());
      }
    }
    return builder.build();
  }

  private ChannelDetails.PaymentModel getPaymentModel(String modelloPagamento) {
    return ChannelDetails.PaymentModel.fromDatabaseValue(modelloPagamento);
  }
}
