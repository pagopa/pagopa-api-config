package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
public class StationMaintenanceService {


    /**
     * Create a station's maintenance for the specified broker with the provided data.
     * <p>
     * Before the creation of the maintenance checks if all the requirements are matched:
     * <ul>
     *     <li> the startDateTime is after 72h from the creation date time
     *     <li> startDateTime < endDateTime
     *     <li> startDateTime and endDateTime are valid only if they are rounded to a 15-minute interval
     *     (seconds and milliseconds are truncated)
     *     <li> there are no overlapping maintenance for the same station
     * </ul>
     * Additionally, it retrieves the count of maintenance hours for the specified broker, and if the annual limit has been reached,
     * the StandIn flag is set to true.
     *
     * @param brokerCode               broker's tax code
     * @param createStationMaintenance info of the maintenance
     * @return the created maintenance
     */
    public StationMaintenanceResource createStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance
    ) {
        return null;
    }

}