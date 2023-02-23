package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
