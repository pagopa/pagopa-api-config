package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
