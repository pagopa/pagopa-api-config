package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.configuration.PaymentType;

public class ConvertPaymentTypeToString implements Converter<PaymentType, String> {
  @Override
  public String convert(MappingContext<PaymentType, String> mappingContext) {
    @Valid PaymentType paymentType = mappingContext.getSource();
    return paymentType.getPaymentTypeCode();
  }
}
