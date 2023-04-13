package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;

public class ConvertTipiVersamentoToPaymentType implements Converter<TipiVersamento, PaymentType> {
  @Override
  public PaymentType convert(MappingContext<TipiVersamento, PaymentType> mappingContext) {
    @Valid TipiVersamento tipiVersamento = mappingContext.getSource();
    return PaymentType.builder()
        .description(tipiVersamento.getDescrizione())
        .paymentTypeCode(tipiVersamento.getTipoVersamento())
        .build();
  }
}
