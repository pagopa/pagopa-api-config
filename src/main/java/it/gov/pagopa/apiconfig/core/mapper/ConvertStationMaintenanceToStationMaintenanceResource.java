package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link StationMaintenance} instance to a {@link StationMaintenanceResource} instance
 */
public class ConvertStationMaintenanceToStationMaintenanceResource implements Converter<StationMaintenance, StationMaintenanceResource> {

    @Override
    public StationMaintenanceResource convert(MappingContext<StationMaintenance, StationMaintenanceResource> context) {
        StationMaintenance model = context.getSource();

        return StationMaintenanceResource.builder()
                .maintenanceId(model.getObjId())
                .brokerCode(model.getStation().getIntermediarioPa().getIdIntermediarioPa())
                .stationCode(model.getStation().getIdStazione())
                .startDateTime(model.getStartDateTime())
                .endDateTime(model.getEndDateTime())
                .standIn(model.getStandIn())
                .build();
    }
}