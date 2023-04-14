package it.gov.pagopa.apiconfig.core.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pdd;

public class ConvertPddEToPddM
    implements Converter<Pdd, it.gov.pagopa.apiconfig.core.model.configuration.Pdd> {
  @Override
  public it.gov.pagopa.apiconfig.core.model.configuration.Pdd convert(
      MappingContext<Pdd, it.gov.pagopa.apiconfig.core.model.configuration.Pdd> context) {
    Pdd source = context.getSource();
    return it.gov.pagopa.apiconfig.core.model.configuration.Pdd.builder()
        .idPdd(source.getIdPdd())
        .enabled(source.getEnabled())
        .description(source.getDescrizione())
        .ip(source.getIp())
        .port(source.getPorta())
        .build();
  }
}
