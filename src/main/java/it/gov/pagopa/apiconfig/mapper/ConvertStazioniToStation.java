package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;

public class ConvertStazioniToStation implements Converter<Stazioni, Station> {

  @Override
  public Station convert(MappingContext<Stazioni, Station> context) {
    @Valid Stazioni source = context.getSource();
    return Station.builder()
        .enabled(source.getEnabled())
        .stationCode(source.getIdStazione())
        .brokerDescription(source.getIntermediarioPa().getCodiceIntermediario())
        .version(source.getVersione())
        .build();
  }
}
