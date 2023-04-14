package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;

public class ConvertCanaleTipoVersamentoToPaymentType
    implements Converter<CanaleTipoVersamento, PaymentType> {
  @Override
  public PaymentType convert(MappingContext<CanaleTipoVersamento, PaymentType> context) {
    @Valid CanaleTipoVersamento source = context.getSource();
    return PaymentType.builder()
        .paymentTypeCode(source.getTipoVersamento().getTipoVersamento())
        .build();
  }
}
