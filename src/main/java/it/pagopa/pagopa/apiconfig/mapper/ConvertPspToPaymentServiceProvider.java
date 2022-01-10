package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPspToPaymentServiceProvider implements Converter<Psp, PaymentServiceProvider> {

    @Override
    public PaymentServiceProvider convert(MappingContext<Psp, PaymentServiceProvider> context) {
        Psp source = context.getSource();
        return PaymentServiceProvider.builder()
                .pspCode(source.getIdPsp())
                .enabled(source.getEnabled())
                .businessName(CommonUtil.deNull(source.getRagioneSociale()))
                .build();
    }
}
