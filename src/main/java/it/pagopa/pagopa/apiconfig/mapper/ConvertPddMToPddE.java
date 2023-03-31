package it.pagopa.pagopa.apiconfig.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pdd;

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
