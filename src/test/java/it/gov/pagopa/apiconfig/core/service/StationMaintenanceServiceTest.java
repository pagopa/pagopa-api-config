package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.config.MappingsConfiguration;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceListResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.UpdateStationMaintenance;
import it.gov.pagopa.apiconfig.core.repository.ExtendedStationMaintenanceRepository;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenance;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryId;
import it.gov.pagopa.apiconfig.starter.entity.StationMaintenanceSummaryView;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.StationMaintenanceSummaryViewRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StationMaintenanceService.class, MappingsConfiguration.class})
class StationMaintenanceServiceTest {

    private static final String BROKER_CODE = "brokerCode";
    private static final String STATION_CODE = "stationCode";
    private static final Long MAINTENANCE_ID = 123L;

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
    void createStationMaintenanceSuccessWithoutStandInOverride() {
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
        assertEquals(createMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(createMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());
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
        assertEquals(createMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(createMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());
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
                OffsetDateTime.now().withMinute(0).truncatedTo(ChronoUnit.MINUTES),
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

    @Test
    void updateStationMaintenanceSuccessScheduledMaintenanceWithoutStandInOverride() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(oldMaintenance));
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewNotExtra()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .standIn(false)
                .build();

        StationMaintenanceResource result = assertDoesNotThrow(() ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertEquals(updateStationMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertEquals(updateStationMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(updateStationMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());
    }

    @Test
    void updateStationMaintenanceSuccessScheduledMaintenanceWithStandInOverride() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(oldMaintenance));
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewExtra()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .standIn(false)
                .build();

        StationMaintenanceResource result = assertDoesNotThrow(() ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertNotEquals(updateStationMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertTrue(savedMaintenance.getStandIn());
        assertEquals(updateStationMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(updateStationMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());
    }

    @Test
    void updateStationMaintenanceSuccessScheduledMaintenanceWithoutChangeStandIn() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(oldMaintenance));
        when(summaryViewRepository.findById(any())).thenReturn(Optional.of(buildSummaryViewExtra()));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plusDays(5).plusHours(2).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        StationMaintenanceResource result = assertDoesNotThrow(() ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertNotEquals(updateStationMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertEquals(oldMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertEquals(updateStationMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(updateStationMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());
    }

    @Test
    void updateStationMaintenanceSuccessInProgressMaintenance() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().minusHours(1));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(oldMaintenance));
        when(stationMaintenanceRepository.save(any())).thenReturn(buildMaintenance());

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plusHours(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .standIn(true)
                .build();

        StationMaintenanceResource result = assertDoesNotThrow(() ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(result);
        assertEquals(BROKER_CODE, result.getBrokerCode());
        assertEquals(STATION_CODE, result.getStationCode());

        verify(stationMaintenanceRepository).save(stationMaintenanceCaptor.capture());
        StationMaintenance savedMaintenance = stationMaintenanceCaptor.getValue();
        assertNotEquals(updateStationMaintenance.getStandIn(), savedMaintenance.getStandIn());
        assertNotEquals(updateStationMaintenance.getStartDateTime(), savedMaintenance.getStartDateTime());
        assertEquals(updateStationMaintenance.getEndDateTime(), savedMaintenance.getEndDateTime());

        verify(summaryViewRepository, never()).findById(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceInvalidStartDateNull() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder().build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceInvalidStartDate() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_START_DATE_TIME_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceStartDateNotRoundedTo15Minutes() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().withMinute(12).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceEndDateNotRoundedTo15Minutes() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().withMinute(34).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceEndDateBeforeStartDate() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailScheduledMaintenanceHasOverlappingMaintenance() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().plusDays(3));
        StationMaintenance overlapping = buildMaintenance();
        overlapping.setObjId(11111L);

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(overlapping));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now().plusDays(5).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .endDateTime(OffsetDateTime.now().plusDays(6).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.title, e.getTitle());

        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailInProgressMaintenanceEndDateNotRoundedTo15Minutes() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().minusHours(1));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .endDateTime(OffsetDateTime.now().withMinute(34).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailInProgressMaintenanceEndDateBeforeCurrentTimestamp() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().minusHours(1));

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .endDateTime(OffsetDateTime.now().minusHours(1).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_NOT_VALID.title, e.getTitle());

        verify(stationMaintenanceRepository, never()).findOverlappingMaintenance(anyString(), any(), any());
        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void updateStationMaintenanceFailInProgressMaintenanceHasOverlappingMaintenance() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().minusHours(1));
        StationMaintenance overlapping = buildMaintenance();
        overlapping.setObjId(11111L);

        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        when(stationMaintenanceRepository.findOverlappingMaintenance(anyString(), any(), any()))
                .thenReturn(Collections.singletonList(overlapping));

        UpdateStationMaintenance updateStationMaintenance = UpdateStationMaintenance.builder()
                .endDateTime(OffsetDateTime.now().plusHours(1).withMinute(0).truncatedTo(ChronoUnit.MINUTES))
                .build();

        AppException e = assertThrows(AppException.class, () ->
                sut.updateStationMaintenance(BROKER_CODE, MAINTENANCE_ID, updateStationMaintenance));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_DATE_TIME_INTERVAL_HAS_OVERLAPPING.title, e.getTitle());

        verify(summaryViewRepository, never()).findById(any());
        verify(stationMaintenanceRepository, never()).save(any());
    }

    @Test
    void getStationMaintenanceSuccessWhenValidData() {
        StationMaintenance oldMaintenance = buildMaintenanceParametrized(OffsetDateTime.now().minusHours(1));
        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(oldMaintenance));
        StationMaintenanceResource result = assertDoesNotThrow(
                () -> sut.getStationMaintenance("test", 0L));
        assertNotNull(result);
        assertEquals(result.getMaintenanceId(), oldMaintenance.getObjId());
    }

    @Test
    void getStationMaintenanceExceptionWhenMissingData() {
        when(stationMaintenanceRepository.findById(anyLong())).thenReturn(Optional.empty());
        Assert.assertThrows(AppException.class, () ->
                sut.getStationMaintenance("test", 0L));
    }

    @Test
    void getStationMaintenancesSuccess() {
        List<StationMaintenance> list = Collections.singletonList(buildMaintenance());
        when(stationMaintenanceRepository.findAllByFilters(
                anyString(),
                anyString(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(new PageImpl<>(list));

        StationMaintenanceListResource result = assertDoesNotThrow(() ->
                sut.getStationMaintenances(
                        BROKER_CODE,
                        STATION_CODE,
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        PageRequest.of(0, 10)
                ));

        assertNotNull(result);
        assertEquals(list.size(), result.getMaintenanceList().size());
        assertEquals(list.size(), result.getPageInfo().getItemsFound());
    }

    @Test
    void getAllStationsMaintenancesSuccess() {
        List<StationMaintenance> list = Collections.singletonList(buildMaintenance());
        when(stationMaintenanceRepository.findAllStationsMaintenances(
                any(),
                any(),
                any(),
                any()
        )).thenReturn(new PageImpl<>(list));

        StationMaintenanceListResource result = assertDoesNotThrow(() ->
                sut.getAllStationsMaintenances(
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                ));

        assertNotNull(result);
        assertEquals(list.size(), result.getMaintenanceList().size());
        assertEquals(list.size(), result.getPageInfo().getItemsFound());
    }

    @Test
    void getBrokerMaintenancesSummarySuccessWithoutExtra() {
        StationMaintenanceSummaryView summaryView = StationMaintenanceSummaryView.builder()
                .usedHours(10.25)
                .scheduledHours(2.50)
                .build();
        when(summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .brokerCode(BROKER_CODE)
                        .maintenanceYear("2024")
                        .build())
        ).thenReturn(Optional.of(summaryView));

        MaintenanceHoursSummaryResource result = assertDoesNotThrow(() ->
                sut.getBrokerMaintenancesSummary(BROKER_CODE, "2024"));

        assertNotNull(result);
        assertEquals("10:15", result.getUsedHours());
        assertEquals("2:30", result.getScheduledHours());
        assertEquals("23:15", result.getRemainingHours());
        assertEquals("0", result.getExtraHours());
        assertEquals("36", result.getAnnualHoursLimit());
    }

    @Test
    void getBrokerMaintenancesSummarySuccessWithExtra() {
        StationMaintenanceSummaryView summaryView = StationMaintenanceSummaryView.builder()
                .usedHours(35.25)
                .scheduledHours(10.50)
                .build();
        when(summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .brokerCode(BROKER_CODE)
                        .maintenanceYear("2024")
                        .build())
        ).thenReturn(Optional.of(summaryView));

        MaintenanceHoursSummaryResource result = assertDoesNotThrow(() ->
                sut.getBrokerMaintenancesSummary(BROKER_CODE, "2024"));

        assertNotNull(result);
        assertEquals("35:15", result.getUsedHours());
        assertEquals("10:30", result.getScheduledHours());
        assertEquals("0", result.getRemainingHours());
        assertEquals("9:45", result.getExtraHours());
        assertEquals("36", result.getAnnualHoursLimit());
    }

    @Test
    void getBrokerMaintenancesSummaryFailNotFound() {
        when(summaryViewRepository.findById(
                StationMaintenanceSummaryId.builder()
                        .brokerCode(BROKER_CODE)
                        .maintenanceYear("2024")
                        .build())
        ).thenReturn(Optional.empty());

        MaintenanceHoursSummaryResource result = assertDoesNotThrow(() ->
                sut.getBrokerMaintenancesSummary(BROKER_CODE, "2024"));

        assertNotNull(result);
        assertEquals("0", result.getUsedHours());
        assertEquals("0", result.getScheduledHours());
        assertEquals("36", result.getRemainingHours());
        assertEquals("0", result.getExtraHours());
        assertEquals("36", result.getAnnualHoursLimit());
    }

    @Test
    void deleteStationMaintenanceSuccess() {
        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.of(buildMaintenance()));

        assertDoesNotThrow(() -> sut.deleteStationMaintenance(BROKER_CODE, MAINTENANCE_ID));
    }

    @Test
    void deleteStationMaintenanceFail() {
        when(stationMaintenanceRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        AppException e = assertThrows(AppException.class, () -> sut.deleteStationMaintenance(BROKER_CODE, MAINTENANCE_ID));

        assertNotNull(e);
        assertEquals(AppError.MAINTENANCE_NOT_FOUND.httpStatus, e.getHttpStatus());
        assertEquals(AppError.MAINTENANCE_NOT_FOUND.title, e.getTitle());
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

    private StationMaintenance buildMaintenanceParametrized(OffsetDateTime startDateTime) {
        return StationMaintenance.builder()
                .objId(MAINTENANCE_ID)
                .station(Stazioni.builder()
                        .idStazione(STATION_CODE)
                        .intermediarioPa(IntermediariPa.builder()
                                .idIntermediarioPa(BROKER_CODE)
                                .build())
                        .build())
                .startDateTime(startDateTime)
                .endDateTime(startDateTime.plusHours(2))
                .standIn(false)
                .build();
    }

    private StationMaintenance buildMaintenance() {
        return StationMaintenance.builder()
                .objId(MAINTENANCE_ID)
                .station(Stazioni.builder()
                        .idStazione(STATION_CODE)
                        .intermediarioPa(IntermediariPa.builder()
                                .idIntermediarioPa(BROKER_CODE)
                                .build())
                        .build())
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
