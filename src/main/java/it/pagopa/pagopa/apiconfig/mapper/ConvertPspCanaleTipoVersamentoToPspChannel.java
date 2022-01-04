package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannel;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertPspCanaleTipoVersamentoToPspChannel implements Converter<PspCanaleTipoVersamento, PspChannel> {

    @Override
    public PspChannel convert(MappingContext<PspCanaleTipoVersamento, PspChannel> context) {
        @Valid PspCanaleTipoVersamento source = context.getSource();
        Canali canale = source.getCanaleTipoVersamento().getCanale();
        return PspChannel.builder()
                .channelCode(canale.getIdCanale())
                .enabled(canale.getEnabled())
                // the mapping of paymentTypeList is custom
                .build();
    }

}
