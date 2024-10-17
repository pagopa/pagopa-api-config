package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceListResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.UpdateStationMaintenance;
import it.gov.pagopa.apiconfig.core.repository.ExtendedStationMaintenanceRepository;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryId;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryView;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.StationMaintenanceSummaryViewRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Validated
@Transactional
public class StationMaintenanceService {

    private final ExtendedStationMaintenanceRepository stationMaintenanceRepository;
    private final StationMaintenanceSummaryViewRepository summaryViewRepository;
    private final StazioniRepository stationRepository;
    private final ModelMapper mapper;
    private final Double annualHoursLimit;
    private final Double minimumSchedulingNoticeHours;

    @Autowired
    public StationMaintenanceService(
            ExtendedStationMaintenanceRepository stationMaintenanceRepository,
            StationMaintenanceSummaryViewRepository summaryViewRepository,
            StazioniRepository stationRepository,
            ModelMapper mapper,
            @Value("${station.maintenance.annual-hours-limit}") Double annualHoursLimit,
            @Value("${station.maintenance.minimum-scheduling-notice-hours}") Double minimumSchedulingNoticeHours
    ) {
        this.stationMaintenanceRepository = stationMaintenanceRepository;
        this.summaryViewRepository = summaryViewRepository;
        this.stationRepository = stationRepository;
        this.mapper = mapper;
        this.annualHoursLimit = annualHoursLimit;
        this.minimumSchedulingNoticeHours = minimumSchedulingNoticeHours;
    }

    /**
     * Create a station's maintenance for the specified broker with the provided data.
     * <p>
     * Before the creation of the maintenance checks if all the requirements are matched:
     * <ul>
     *     <li> the startDateTime is after {@link #minimumSchedulingNoticeHours} from the creation date time
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

        if (isNotRoundedTo15Minutes(startDateTime) || isNotRoundedTo15Minutes(endDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Date time are not rounded to 15 minutes");
        }
        if (computeDateDifferenceInHours(now, startDateTime) < minimumSchedulingNoticeHours) {
            throw new AppException(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID);
        }
        if (!endDateTime.isAfter(startDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Start date time must be before end date time");
        }
        if (hasOverlappingMaintenance(createStationMaintenance.getStationCode(), startDateTime, endDateTime, null)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING);
        }

        StationMaintenance maintenance = buildStationMaintenance(brokerCode, createStationMaintenance, now, startDateTime, endDateTime);
        StationMaintenance saved = this.stationMaintenanceRepository.save(maintenance);

        return this.mapper.map(saved, StationMaintenanceResource.class);
    }

    /**
     * Update the station's maintenance with the specified maintenance id with the provided data.
     * <p>
     * If the maintenance is already in progress, only the endDateTime field can be
     * updated otherwise startDateTime, endDateTime and standIn fields can be updated.
     * Before the update of the maintenance checks if all the requirements are matched, if it is an in progress maintenance
     * perform the following checks:
     * <ul>
     *     <li> endDateTime is valid only if it is rounded to a 15-minute interval (seconds and milliseconds are truncated)
     *     <li> current timestamp < endDateTime
     *     <li> there are no overlapping maintenance for the same station excluded the current maintenance
     * </ul>
     * Otherwise, perform the following checks:
     * <ul>
     *     <li> the startDateTime is after {@link #minimumSchedulingNoticeHours} from the creation date time
     *     <li> startDateTime and endDateTime are valid only if they are rounded to a 15-minute interval
     *     (seconds and milliseconds are truncated)
     *     <li> startDateTime < endDateTime
     *     <li> there are no overlapping maintenance for the same station excluded the current maintenance
     * </ul>
     * Additionally, in case of scheduled maintenance it retrieves the count of maintenance hours for the specified broker,
     * and if the annual limit has been reached, the StandIn flag is set to true.
     *
     * @param brokerCode               broker's tax code
     * @param maintenanceId            maintenance's id
     * @param updateStationMaintenance update info of the maintenance
     * @return the updated maintenance
     */
    public StationMaintenanceResource updateStationMaintenance(
            String brokerCode,
            Long maintenanceId,
            UpdateStationMaintenance updateStationMaintenance
    ) {
        StationMaintenance stationMaintenance = this.stationMaintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new AppException(AppError.MAINTENANCE_NOT_FOUND, maintenanceId));
        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        StationMaintenance saved;
        boolean isMaintenanceInProgress = stationMaintenance.getStartDateTime().isBefore(now.plusMinutes(15));
        if (isMaintenanceInProgress) {
            saved = updateInProgressStationMaintenance(now, updateStationMaintenance, stationMaintenance);
        } else {
            saved = updateScheduledStationMaintenance(brokerCode, now, updateStationMaintenance, stationMaintenance);
        }

        return this.mapper.map(saved, StationMaintenanceResource.class);
    }

    /**
     * Retrieve a paginated list of station maintenance for the specified broker with the provided filters.
     *
     * @param brokerCode          broker's tax code
     * @param stationCode         station's code
     * @param startDateTimeBefore used to filter out all maintenance that have the start date time before this date time
     * @param startDateTimeAfter  used to filter out all maintenance that have the start date time after this date time
     * @param endDateTimeBefore   used to filter out all maintenance that have the end date time before this date time
     * @param endDateTimeAfter    used to filter out all maintenance that have the end date time after this date time
     * @param pageable            contains info about the requested page
     * @return the requested page of maintenances
     */
    public StationMaintenanceListResource getStationMaintenances(
            String brokerCode,
            String stationCode,
            OffsetDateTime startDateTimeBefore,
            OffsetDateTime startDateTimeAfter,
            OffsetDateTime endDateTimeBefore,
            OffsetDateTime endDateTimeAfter,
            Pageable pageable
    ) {
        Page<StationMaintenance> response = this.stationMaintenanceRepository.findAllByFilters(
                brokerCode,
                stationCode,
                startDateTimeBefore,
                startDateTimeAfter,
                endDateTimeBefore,
                endDateTimeAfter,
                pageable
        );
        List<StationMaintenanceResource> maintenanceList = response.getContent().parallelStream()
                .map(maintenance -> this.mapper.map(maintenance, StationMaintenanceResource.class))
                .toList();

        return StationMaintenanceListResource.builder()
                .maintenanceList(maintenanceList)
                .pageInfo(PageInfo.builder()
                        .page(pageable.getPageNumber())
                        .limit(pageable.getPageSize())
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    /**
     * Retrieve a list of all stations' maintenances with the provided filters.
     *
     * @param startDateTimeBefore used to filter out all maintenance that have the start date time before this date time
     * @param startDateTimeAfter  used to filter out all maintenance that have the start date time after this date time
     * @param endDateTimeBefore   used to filter out all maintenance that have the end date time before this date time
     * @param endDateTimeAfter    used to filter out all maintenance that have the end date time after this date time
     * @return the requested page of maintenances
     */
    public StationMaintenanceListResource getAllStationsMaintenances(
            OffsetDateTime startDateTimeBefore,
            OffsetDateTime startDateTimeAfter,
            OffsetDateTime endDateTimeBefore,
            OffsetDateTime endDateTimeAfter
    ) {
        Page<StationMaintenance> response = this.stationMaintenanceRepository.findAllStationsMaintenances(
                startDateTimeBefore,
                startDateTimeAfter,
                endDateTimeBefore,
                endDateTimeAfter
        );
        List<StationMaintenanceResource> maintenanceList = response.getContent().parallelStream()
                .map(maintenance -> this.mapper.map(maintenance, StationMaintenanceResource.class))
                .toList();

        return StationMaintenanceListResource.builder()
                .maintenanceList(maintenanceList)
                .pageInfo(PageInfo.builder()
                        .totalItems(response.getTotalElements())
                        .totalPages(response.getTotalPages())
                        .itemsFound(response.getNumberOfElements())
                        .build())
                .build();
    }

    /**
     * Retrieve the maintenance's hours summary of the specified broker for the provided year
     *
     * @param brokerCode      broker's tax code
     * @param maintenanceYear year of maintenance for the summary
     * @return the summary
     */
    public MaintenanceHoursSummaryResource getBrokerMaintenancesSummary(String brokerCode, String maintenanceYear) {
        StationMaintenanceSummaryView maintenanceSummary = this.summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .maintenanceYear(maintenanceYear)
                        .brokerCode(brokerCode)
                        .build()
        ).orElse(buildEmptySummary());

        Double usedHours = maintenanceSummary.getUsedHours();
        Double scheduledHours = maintenanceSummary.getScheduledHours();
        double remainingHours = 0;
        double extraHours = 0;
        if (usedHours + scheduledHours < annualHoursLimit) {
            remainingHours = annualHoursLimit - (scheduledHours + usedHours);
        } else {
            extraHours = (scheduledHours + usedHours) - annualHoursLimit;
        }

        return MaintenanceHoursSummaryResource.builder()
                .usedHours(transformHoursToStringFormat(usedHours))
                .scheduledHours(transformHoursToStringFormat(scheduledHours))
                .remainingHours(transformHoursToStringFormat(remainingHours))
                .extraHours(transformHoursToStringFormat(extraHours))
                .annualHoursLimit(transformHoursToStringFormat(annualHoursLimit))
                .build();
    }

    /**
     * Recovers a station maintenance, given its brokerCode and maintenanceId.
     * If the provided brokerCode doesn't match the one related to the persisted one for the given maintenance,
     * it will throw the maintenance not found exception
     *
     * @param brokerCode    brokerCode to be used as filter in the maintenance recovery
     * @param maintenanceId station maintenance id to be used for the detail recovery
     * @return station maintenance data, provided in an instance of StationMaintenanceResource
     * @throws AppException thrown when a maintenance, given the input data, has not been found
     */
    public StationMaintenanceResource getStationMaintenance(String brokerCode, Long maintenanceId) {
        StationMaintenance stationMaintenance = this.stationMaintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new AppException(AppError.MAINTENANCE_NOT_FOUND, maintenanceId));

        return this.mapper.map(stationMaintenance, StationMaintenanceResource.class);
    }

    /**
     * Delete the maintenance with the provided id
     *
     * @param brokerCode    broker's tax code
     * @param maintenanceId maintenance's id
     */
    public void deleteStationMaintenance(String brokerCode, Long maintenanceId) {
        StationMaintenance stationMaintenance = this.stationMaintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new AppException(AppError.MAINTENANCE_NOT_FOUND, maintenanceId));
        this.stationMaintenanceRepository.delete(stationMaintenance);
    }

    private StationMaintenance updateScheduledStationMaintenance(
            String brokerCode,
            OffsetDateTime now,
            UpdateStationMaintenance updateStationMaintenance,
            StationMaintenance oldStationMaintenance
    ) {
        if (updateStationMaintenance.getStartDateTime() == null) {
            throw new AppException(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID);
        }

        OffsetDateTime newStartDateTime = updateStationMaintenance.getStartDateTime().truncatedTo(ChronoUnit.MINUTES);
        OffsetDateTime newEndDateTime = updateStationMaintenance.getEndDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (isNotRoundedTo15Minutes(newStartDateTime) || isNotRoundedTo15Minutes(newEndDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Date time are not rounded to 15 minutes");
        }
        if (computeDateDifferenceInHours(now, newStartDateTime) < minimumSchedulingNoticeHours) {
            throw new AppException(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID);
        }
        if (!newEndDateTime.isAfter(newStartDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "Start date time must be before end date time");
        }
        if (hasOverlappingMaintenance(
                oldStationMaintenance.getStation().getIdStazione(),
                newStartDateTime,
                newEndDateTime,
                oldStationMaintenance.getObjId()
        )) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING);
        }

        boolean standIn = updateStationMaintenance.getStandIn() != null
                ? updateStationMaintenance.getStandIn()
                : oldStationMaintenance.getStandIn();
        double oldScheduledHours = computeDateDifferenceInHours(oldStationMaintenance.getStartDateTime(), oldStationMaintenance.getEndDateTime());
        // force standIn flag to true when the used has already consumed all the available hours for this year
        if (isAnnualHoursLimitExceededForUser(brokerCode, newStartDateTime, newEndDateTime, oldScheduledHours, String.valueOf(now.getYear()))) {
            standIn = true;
        }

        oldStationMaintenance.setStartDateTime(newStartDateTime);
        oldStationMaintenance.setEndDateTime(newEndDateTime);
        oldStationMaintenance.setStandIn(standIn);
        return this.stationMaintenanceRepository.save(oldStationMaintenance);
    }

    private StationMaintenance updateInProgressStationMaintenance(
            OffsetDateTime now,
            UpdateStationMaintenance updateStationMaintenance,
            StationMaintenance oldStationMaintenance
    ) {
        OffsetDateTime newEndDateTime = updateStationMaintenance.getEndDateTime().truncatedTo(ChronoUnit.MINUTES);

        if (isNotRoundedTo15Minutes(newEndDateTime)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID, "End date time is not rounded to 15 minutes");
        }
        if (!newEndDateTime.isAfter(now)) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID,
                    "End date time of an in progress maintenance can not be before current timestamp");
        }
        if (hasOverlappingMaintenance(
                oldStationMaintenance.getStation().getIdStazione(),
                oldStationMaintenance.getStartDateTime(),
                newEndDateTime,
                oldStationMaintenance.getObjId()
        )) {
            throw new AppException(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING);
        }

        oldStationMaintenance.setEndDateTime(newEndDateTime);
        return this.stationMaintenanceRepository.save(oldStationMaintenance);
    }

    private StationMaintenance buildStationMaintenance(
            String brokerCode,
            CreateStationMaintenance createStationMaintenance,
            OffsetDateTime now,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    ) {
        boolean standIn = createStationMaintenance.getStandIn();
        // force standIn flag to true when the used has already consumed all the available hours for this year
        if (isAnnualHoursLimitExceededForUser(brokerCode, startDateTime, endDateTime, 0, String.valueOf(now.getYear()))) {
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
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            double oldScheduledHours,
            String maintenanceYear
    ) {
        Optional<StationMaintenanceSummaryView> maintenanceSummaryViewOptional = this.summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .maintenanceYear(maintenanceYear)
                        .brokerCode(brokerCode)
                        .build()
        );
        if (maintenanceSummaryViewOptional.isEmpty()) {
            return false;
        }
        StationMaintenanceSummaryView maintenanceSummary = maintenanceSummaryViewOptional.get();
        double consumedHours = maintenanceSummary.getUsedHours() + maintenanceSummary.getScheduledHours();
        double newHoursToBeScheduled = computeDateDifferenceInHours(startDateTime, endDateTime);

        return (consumedHours - oldScheduledHours + newHoursToBeScheduled) > annualHoursLimit;
    }

    private boolean hasOverlappingMaintenance(
            String stationCode,
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime,
            Long excludedMaintenance
    ) {
        return !this.stationMaintenanceRepository
                .findOverlappingMaintenance(stationCode, startDateTime, endDateTime)
                .parallelStream()
                .filter(maintenance -> excludedMaintenance == null || !maintenance.getObjId().equals(excludedMaintenance))
                .toList()
                .isEmpty();
    }

    private boolean isNotRoundedTo15Minutes(OffsetDateTime dateTime) {
        return dateTime.getMinute() % 15 != 0;
    }

    private String transformHoursToStringFormat(Double hours) {
        if (hours == 0) {
            return "0";
        }
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(hours));
        int intValue = bigDecimal.intValue();
        BigDecimal decimal = bigDecimal.subtract(new BigDecimal(intValue));

        if (decimal.compareTo(BigDecimal.valueOf(0.25)) == 0) {
            return String.format("%s:15", intValue);
        }
        if (decimal.compareTo(BigDecimal.valueOf(0.50)) == 0) {
            return String.format("%s:30", intValue);
        }
        if (decimal.compareTo(BigDecimal.valueOf(0.75)) == 0) {
            return String.format("%s:45", intValue);
        }
        return String.valueOf(intValue);
    }

    private StationMaintenanceSummaryView buildEmptySummary() {
        return StationMaintenanceSummaryView.builder()
                .usedHours(0.0)
                .scheduledHours(0.0)
                .build();
    }
}
