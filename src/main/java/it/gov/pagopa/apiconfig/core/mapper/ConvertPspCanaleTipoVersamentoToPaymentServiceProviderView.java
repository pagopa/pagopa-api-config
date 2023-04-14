package it.gov.pagopa.apiconfig.core.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProviderView;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;

public class ConvertPspCanaleTipoVersamentoToPaymentServiceProviderView implements Converter<PspCanaleTipoVersamento, PaymentServiceProviderView> {

  @Override
  public PaymentServiceProviderView convert(MappingContext<PspCanaleTipoVersamento, PaymentServiceProviderView> context) {
    PspCanaleTipoVersamento source = context.getSource();
    return PaymentServiceProviderView.builder()
        .pspCode(source.getPsp().getIdPsp())
        .brokerPspCode(source.getCanaleTipoVersamento().getCanale().getFkIntermediarioPsp().getIdIntermediarioPsp())
        .channelCode(source.getCanaleTipoVersamento().getCanale().getIdCanale())
        .paymentType(source.getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento())
        .paymentMethod(source.getCanaleTipoVersamento().getCanale().getFkCanaliNodo().getModelloPagamento())
        .build();
  }
}
