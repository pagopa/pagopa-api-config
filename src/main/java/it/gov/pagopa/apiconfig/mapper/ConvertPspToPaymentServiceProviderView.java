package it.gov.pagopa.apiconfig.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviderView;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProvidersViewExtraInfos;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;

public class ConvertPspToPaymentServiceProviderView implements Converter<Psp, PaymentServiceProviderView> {

  @Override
  public PaymentServiceProviderView convert(MappingContext<Psp, PaymentServiceProviderView> context) {
    Psp source = context.getSource();
    List<PaymentServiceProvidersViewExtraInfos> extraInfos = new ArrayList<>();
    for (PspCanaleTipoVersamento pspCTV: source.getPspCanaleTipoVersamentoList()) {
      extraInfos.add(
          PaymentServiceProvidersViewExtraInfos.builder()
          .brokerPspCode(pspCTV.getCanaleTipoVersamento().getCanale().getFkIntermediarioPsp().getIdIntermediarioPsp())
          .channelCode(pspCTV.getCanaleTipoVersamento().getCanale().getIdCanale())
          .paymentType(pspCTV.getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento())
          .paymentMethod(pspCTV.getCanaleTipoVersamento().getCanale().getFkCanaliNodo().getModelloPagamento())
          .build());
    }
    return PaymentServiceProviderView.builder()
        .pspCode(source.getIdPsp())
        .pspViewInfos(extraInfos)
        .build();
  }
}
