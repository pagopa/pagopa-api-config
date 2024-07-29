package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.repository.ExtendedStationMaintenanceRepository;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryView;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.StationMaintenanceSummaryViewRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = StationMaintenanceService.class)
class StationMaintenanceServiceTest {

    private static final String BROKER_CODE = "brokerCode";
    private static final String STATION_CODE = "stationCode";

    @MockBean
    private ExtendedStationMaintenanceRepository stationMaintenanceRepository;

    @MockBean
    private StationMaintenanceSummaryViewRepository summaryViewRepository;

    @MockBean
    private StazioniRepository stationRepository;

    @Captor
    private ArgumentCaptor<StationMaintenance> stationMaintenanceCaptor;

    @Autowired
    private StationMaintenanceService sut;

    @Test
    void createStationMaintenanceSuccessWithNoStandInOverride() {
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any())).thenReturn(Collections.emptyList());
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewNotExtra()));
        when(stationRepository.findByIdStazione(STATION_CODE)).thenReturn(Optional.of(new Stazioni()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        StationMaintenanceResource result = assertDoesNotThrow(() -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertEquals(createMaintenance.getStandIn(), savedMaintenance.getStandIn());
    }

    @Test
    void createStationMaintenanceSuccessWithStandInOverride() {
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any())).thenReturn(Collections.emptyList());
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewExtra()));
        when(stationRepository.findByIdStazione(STATION_CODE)).thenReturn(Optional.of(new Stazioni()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        StationMaintenanceResource result = assertDoesNotThrow(() -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertNotEquals(createMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertTrue(savedMaintenance.getStandIn());
    }

    @Test
    void createStationMaintenanceSuccessWithIgnoredSecondsAndMilliseconds() {
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any())).thenReturn(Collections.emptyList());
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewNotExtra()));
        when(stationRepository.findByIdStazione(STATION_CODE)).thenReturn(Optional.of(new Stazioni()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).withSecond(23).withNano(1234),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).withSecond(34).withNano(7734)
        );

        StationMaintenanceResource result = assertDoesNotThrow(() -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertEquals(createMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertEquals(0, savedMaintenance.getStartDateTime().getSecond());
        assertEquals(0, savedMaintenance.getStartDateTime().getNano());
        assertEquals(0, savedMaintenance.getEndDateTime().getSecond());
        assertEquals(0, savedMaintenance.getEndDateTime().getNano());
    }

    @Test
    void createStationMaintenanceFailInvalidStartDate() {
        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void createStationMaintenanceFailStartDateNotRoundedTo15Minutes() {
        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(23).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void createStationMaintenanceFailEndDateNotRoundedTo15Minutes() {
        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(17),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void createStationMaintenanceFailStartDateBeforeEndDateTime() {
        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).minusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void createStationMaintenanceFailStartDateEqualEndDateTime() {
        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void createStationMaintenanceFailHasOverlappingMaintenance() {
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(buildMaintenance()));

        CreateStationMaintenance createMaintenance = buildCreateStationMaintenance(
                OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES),
                OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES)
        );

        AppException e = assertThrows(AppException.class, () -> sut.createStationMaintenance(BROKER_CODE, createMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.title, e.getTitle());

        verify(summaryViewRepository, never()).findById(any());
        verify(stationRepository, never()).findByIdStazione(STATION_CODE);
        verify(stationMaintenanceRepository, never()).save(any());
    }

    private StationMaintenanceSummaryView buildSummaryViewNotExtra() {
        return StationMaintenanceSummaryView.builder()
                .scheduledHours(10.0)
                .usedHours(5.0)
                .build();
    }

    private StationMaintenanceSummaryView buildSummaryViewExtra() {
        return StationMaintenanceSummaryView.builder()
                .scheduledHours(30.0)
                .usedHours(10.0)
                .build();
    }

    private StationMaintenance buildMaintenance() {
        return StationMaintenance.builder()
                .objId(123L)
                .station(new Stazioni())
                .startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now())
                .standIn(false)
                .build();
    }

    private CreateStationMaintenance buildCreateStationMaintenance(
            OffsetDateTime startDateTime,
            OffsetDateTime endDateTime
    ) {
        return CreateStationMaintenance.builder()
                .stationCode(STATION_CODE)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .standIn(false)
                .build();
    }
}