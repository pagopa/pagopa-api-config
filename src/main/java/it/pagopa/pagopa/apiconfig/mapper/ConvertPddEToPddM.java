package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pdd;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPddEToPddM
    implements Converter<Pdd, it.pagopa.pagopa.apiconfig.model.configuration.Pdd> {
  @Override
  public it.pagopa.pagopa.apiconfig.model.configuration.Pdd convert(
      MappingContext<Pdd, it.pagopa.pagopa.apiconfig.model.configuration.Pdd> context) {
    Pdd source = context.getSource();
    return it.pagopa.pagopa.apiconfig.model.configuration.Pdd.builder()
        .idPdd(source.getIdPdd())
        .enabled(source.getEnabled())
        .description(source.getDescrizione())
        .ip(source.getIp())
        .port(source.getPorta())
        .build();
  }
}
