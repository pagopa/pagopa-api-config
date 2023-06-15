package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
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
