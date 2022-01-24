package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertCanaleTipoVersamentoToPaymentTypeCode implements Converter<CanaleTipoVersamento, Service.PaymentTypeCode> {
    @Override
    public Service.PaymentTypeCode convert(MappingContext<CanaleTipoVersamento, Service.PaymentTypeCode> context) {
        @Valid CanaleTipoVersamento source = context.getSource();
        return Service.PaymentTypeCode.valueOf(source.getTipoVersamento().getTipoVersamento());
    }
}
