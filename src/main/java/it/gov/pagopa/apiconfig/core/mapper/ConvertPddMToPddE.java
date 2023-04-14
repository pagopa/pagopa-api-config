package it.gov.pagopa.apiconfig.core.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pdd;

public class ConvertPddMToPddE
    implements Converter<it.gov.pagopa.apiconfig.core.model.configuration.Pdd, Pdd> {
  @Override
  public Pdd convert(
      MappingContext<it.gov.pagopa.apiconfig.core.model.configuration.Pdd, Pdd> context) {
    it.gov.pagopa.apiconfig.core.model.configuration.Pdd source = context.getSource();
    return Pdd.builder()
        .idPdd(source.getIdPdd())
        .enabled(source.getEnabled())
        .descrizione(source.getDescription())
        .ip(source.getIp())
        .porta(source.getPort())
        .build();
  }
}
