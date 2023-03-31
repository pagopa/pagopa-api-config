package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;

public class ConvertPaymentTypeToTipiVersamento implements Converter<PaymentType, TipiVersamento> {
  @Override
  public TipiVersamento convert(MappingContext<PaymentType, TipiVersamento> mappingContext) {
    @Valid PaymentType paymentType = mappingContext.getSource();
    return TipiVersamento.builder()
        .tipoVersamento(paymentType.getPaymentTypeCode())
        .descrizione(paymentType.getDescription())
        .build();
  }
}
