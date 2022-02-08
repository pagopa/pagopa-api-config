package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertPaymentTypeToString implements Converter<PaymentType, String> {
    @Override
    public String convert(MappingContext<PaymentType, String> mappingContext) {
        @Valid PaymentType paymentType = mappingContext.getSource();
        return paymentType.getPaymentTypeCode();
    }
}
