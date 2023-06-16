package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.configuration.PaymentType;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaymentTypeToString implements Converter<PaymentType, String> {
  @Override
  public String convert(MappingContext<PaymentType, String> mappingContext) {
    @Valid PaymentType paymentType = mappingContext.getSource();
    return paymentType.getPaymentTypeCode();
  }
}
