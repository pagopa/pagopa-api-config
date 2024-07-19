package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.repository.ExtendedStationMaintenanceRepository;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryId;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryView;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.StationMaintenanceSummaryViewRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Validated
@Transactional
public class StationMaintenanceService {

    private final ExtendedStationMaintenanceRepository stationMaintenanceRepository;
    private final StationMaintenanceSummaryViewRepository summaryViewRepository;
    private final StazioniRepository stationRepository;

    @Autowired
    public StationMaintenanceService(
            ExtendedStationMaintenanceRepository stationMaintenanceRepository,
            StationMaintenanceSummaryViewRepository summaryViewRepository,
            StazioniRepository stationRepository
    ) {
        this.stationMaintenanceRepository = stationMaintenanceRepository;
        this.summaryViewRepository = summaryViewRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Create a station's maintenance for the specified broker with the provided data.
     * <p>
     * Before the creation of the maintenance checks if all the requirements are matched:
     * <ul>
     *     <li> the startDateTime is after 72h from the creation date time
     *     <li> startDateTime and endDateTime are valid only if they are rounded to a 15-minute interval
     *     (seconds and milliseconds are truncated)
     *     <li> startDateTime < endDateTime
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

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime startDateTime = createStationMaintenance.getStartDateTime().truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime endDateTime = createStationMaintenance.getEndDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (computeDateDifferenceInHours(now, startDateTime) < 72) {
            throw new AppException(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID);
        }
        if (isNotRoundedTo15Minutes(startDateTime) || isNotRoundedTo15Minutes(endDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Date time are not rounded to 15 minutes");
        }
        if (startDateTime.isAfter(endDateTime) || startDateTime.isEqual(endDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Start date time must be before end date time");
        }
        if (hasOverlappingMaintenance(createStationMaintenance, startDateTime, endDateTime)) {
            throw new AppException(
                    AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID,
                    "There is an overlapping maintenance for the same date time interval"
            );
        }

        StationMaintenance maintenance = buildStationMaintenance(brokerCode, createStationMaintenance, now, startDateTime, endDateTime);
        StationMaintenance saved = this.stationMaintenanceRepository.save(maintenance);

        return StationMaintenanceResource.builder()
                .maintenanceId(saved.getObjId())
                .brokerCode(brokerCode)
                .startDateTime(saved.getStartDateTime())
                .endDateTime(saved.getEndDateTime())
                .standIn(saved.getStandIn())
                .stationCode(createStationMaintenance.getStationCode())
                .build();
    }

    private StationMaintenance buildStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance,
            OffsetDateTime now,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    ) {
        boolean standIn = createStationMaintenance.getStandIn();
        // TODO check
        // force standIn flag to true when the used has already consumed all the available hours for this year
        if (isAnnualHoursLimitExceededForUser(brokerCode, now, startDateTime, endDateTime)) {
            standIn = true;
        }

        return StationMaintenance.builder()
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .standIn(standIn)
                .station(getStationByStationCode(createStationMaintenance.getStationCode()))
                .build();
    }

    private double computeDateDifferenceInHours(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime) / 60.00;
    }

    private Stazioni getStationByStationCode(String stationCode) {
        return this.stationRepository.findByIdStazione(stationCode)
                .orElseThrow(() -> new AppException(AppError.STATION_NOT_FOUND, stationCode));
    }

    private boolean isAnnualHoursLimitExceededForUser(
            String brokerCode,
            OffsetDateTime now,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    ) {
        StationMaintenanceSummaryView maintenanceSummary = this.summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .maintenanceYear(String.valueOf(now.getYear()))
                        .brokerCode(brokerCode)
                        .build()
        ).orElseThrow(() -> new AppException(AppError.MAINTENANCE_SUMMARY_NOT_FOUND, brokerCode, ""));
        double consumedHours = maintenanceSummary.getUsedHours() + maintenanceSummary.getScheduledHours();
        double newHoursToBeScheduled = computeDateDifferenceInHours(startDateTime, endDateTime);

        return (consumedHours + newHoursToBeScheduled) > 36;

    }

    private boolean hasOverlappingMaintenance(
            CreateStationMaintenance createStationMaintenance,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    ) {
        return !this.stationMaintenanceRepository
                .findOverlappingMaintenance(createStationMaintenance.getStationCode(), startDateTime, endDateTime)
                .isEmpty();
    }

    private boolean isNotRoundedTo15Minutes(OffsetDateTime dateTime) {
        return dateTime.getMinute() % 15 != 0;
    }

}