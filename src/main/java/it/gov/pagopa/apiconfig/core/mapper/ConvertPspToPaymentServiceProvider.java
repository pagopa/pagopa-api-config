package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
