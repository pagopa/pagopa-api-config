package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.PspChannel;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPspCanaleTipoVersamentoToPspChannel
    implements Converter<PspCanaleTipoVersamento, PspChannel> {

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
