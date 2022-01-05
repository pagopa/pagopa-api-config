package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.util.Optional;

public class ConvertPspToPaymentServiceProvider implements Converter<Psp, PaymentServiceProvider> {

    @Override
    public PaymentServiceProvider convert(MappingContext<Psp, PaymentServiceProvider> context) {
        Psp source = context.getSource();
        return PaymentServiceProvider.builder()
                .pspCode(source.getIdPsp())
                .enabled(source.getEnabled())
                .businessName(Optional.ofNullable(source.getRagioneSociale()).orElse(""))
                .build();
    }
}
