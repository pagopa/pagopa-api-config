package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pdd;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPddMToPddE
    implements Converter<it.pagopa.pagopa.apiconfig.model.configuration.Pdd, Pdd> {
  @Override
  public Pdd convert(
      MappingContext<it.pagopa.pagopa.apiconfig.model.configuration.Pdd, Pdd> context) {
    it.pagopa.pagopa.apiconfig.model.configuration.Pdd source = context.getSource();
    return Pdd.builder()
        .idPdd(source.getIdPdd())
        .enabled(source.getEnabled())
        .descrizione(source.getDescription())
        .ip(source.getIp())
        .porta(source.getPort())
        .build();
  }
}
