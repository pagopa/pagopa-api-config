package it.pagopa.pagopa.apiconfig.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;

public class ConvertPspToPaymentServiceProvider implements Converter<Psp, PaymentServiceProvider> {

  @Override
  public PaymentServiceProvider convert(MappingContext<Psp, PaymentServiceProvider> context) {
    Psp source = context.getSource();
    return PaymentServiceProvider.builder()
        .pspCode(source.getIdPsp())
        .enabled(source.getEnabled())
        .businessName(CommonUtil.deNull(source.getRagioneSociale()))
        .build();
  }
}
