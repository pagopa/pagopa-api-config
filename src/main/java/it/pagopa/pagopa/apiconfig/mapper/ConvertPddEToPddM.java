package it.pagopa.pagopa.apiconfig.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pdd;

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
