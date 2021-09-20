package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.Station;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertStazioniToStation implements Converter<Stazioni, Station> {

    @Override
    public Station convert(MappingContext<Stazioni, Station> context) {
        @Valid Stazioni source = context.getSource();
        return Station.builder()
                .enabled(CommonUtil.flagToBoolean(source.getEnabled()))
                .idStation(source.getIdStazione())
                .version(source.getVersione())
                .build();
    }
}
