package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertCanaleTipoVersamentoToPaymentType implements Converter<CanaleTipoVersamento, PaymentType> {
    @Override
    public PaymentType convert(MappingContext<CanaleTipoVersamento, PaymentType> context) {
        @Valid CanaleTipoVersamento source = context.getSource();
        return PaymentType.builder()
                .paymentTypeCode(source.getTipoVersamento().getTipoVersamento())
                .build();
    }
}
