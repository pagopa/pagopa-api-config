package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.isConnectionSync;
import static it.gov.pagopa.apiconfig.core.util.CommonUtil.toOffsetDateTime;

public class ConvertStazioniToStation implements Converter<Stazioni, Station> {

    @Override
    public Station convert(MappingContext<Stazioni, Station> context) {
        @Valid Stazioni source = context.getSource();
        return Station.builder()
                .enabled(source.getEnabled())
                .stationCode(source.getIdStazione())
                .brokerDescription(source.getIntermediarioPa().getCodiceIntermediario())
                .version(source.getVersione())
                .isConnectionSync(isConnectionSync(source))
                .createDate(toOffsetDateTime(source.getDataCreazione()))
                .build();
    }
}
