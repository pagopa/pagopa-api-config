package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

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
