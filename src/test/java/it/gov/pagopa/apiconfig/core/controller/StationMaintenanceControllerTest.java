package it.gov.pagopa.apiconfig.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.AffectedCreditorInstitutionResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.AggregatedStationMaintenanceListResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.AggregatedStationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.MaintenanceHoursSummaryResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceListResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.StationMaintenanceResource;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.UpdateStationMaintenance;
import it.gov.pagopa.apiconfig.core.service.StationMaintenanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StationMaintenanceControllerTest {

    private static final String BROKER_CODE = "brokerCode";
    private static final String STATION_CODE = "stationCode";
    private static final Long MAINTENANCE_ID = 123L;

    @MockBean
    private StationMaintenanceService stationMaintenanceService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void createStationMaintenanceTest() throws Exception {
        when(stationMaintenanceService.createStationMaintenance(anyString(), any())).thenReturn(buildMaintenanceResource());

        mockMvc.perform(post("/brokers/{brokercode}/station-maintenances", BROKER_CODE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(buildCreateStationMaintenance()))
                ).andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateStationMaintenanceTest() throws Exception {
        when(stationMaintenanceService.updateStationMaintenance(anyString(), anyLong(), any()))
                .thenReturn(buildMaintenanceResource());

        mockMvc.perform(put("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(buildUpdateStationMaintenance()))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStationMaintenanceTest() throws Exception {
        when(stationMaintenanceService.getStationMaintenance(anyString(), anyLong()))
                .thenReturn(buildMaintenanceResource());

        mockMvc.perform(get("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(buildUpdateStationMaintenance()))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStationMaintenanceTestOnKO() throws Exception {
        when(stationMaintenanceService.getStationMaintenance(anyString(), anyLong()))
                .thenThrow(new AppException(AppError.STATION_NOT_FOUND, "test"));

        mockMvc.perform(get("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(buildUpdateStationMaintenance()))
                ).andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStationMaintenancesTest() throws Exception {
        when(stationMaintenanceService.getStationMaintenances(anyString(), anyString(), any(), any(), any(), any(), any()))
                .thenReturn(StationMaintenanceListResource.builder()
                        .maintenanceList(Collections.singletonList(buildMaintenanceResource()))
                        .pageInfo(buildPageInfo())
                        .build());

        mockMvc.perform(get("/brokers/{brokercode}/station-maintenances", BROKER_CODE)
                        .param("stationCode", STATION_CODE)
                        .param("startDateTimeBefore", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("startDateTimeAfter", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeBefore", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeAfter", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("limit", "10")
                        .param("page", "0")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllStationsMaintenancesTest() throws Exception {
        when(stationMaintenanceService.getAllStationsMaintenances(any(), any(), any(), any()))
                .thenReturn(StationMaintenanceListResource.builder()
                        .maintenanceList(Collections.singletonList(buildMaintenanceResource()))
                        .pageInfo(buildPageInfo())
                        .build());

        mockMvc.perform(get("/brokers/station-maintenances")
                        .param("startDateTimeBefore", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("startDateTimeAfter", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeBefore", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeAfter", OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAggregatedStationMaintenancesTest() throws Exception {
        OffsetDateTime startDateTimeBefore = OffsetDateTime.parse("2026-03-01T10:00:00Z");
        OffsetDateTime startDateTimeAfter = OffsetDateTime.parse("2026-03-01T11:00:00Z");
        OffsetDateTime endDateTimeBefore = OffsetDateTime.parse("2026-03-01T12:00:00Z");
        OffsetDateTime endDateTimeAfter = OffsetDateTime.parse("2026-03-01T13:00:00Z");
        when(stationMaintenanceService.getAggregatedStationMaintenances(any(), any(), any(), any()))
                .thenReturn(AggregatedStationMaintenanceListResource.builder()
                        .maintenanceList(Collections.emptyList())
                        .pageInfo(PageInfo.builder().page(0).itemsFound(0).totalItems(0L).totalPages(1).build())
                        .build());

        mockMvc.perform(get("/brokers/station-maintenances/aggregated")
                        .param("startDateTimeBefore", startDateTimeBefore.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("startDateTimeAfter", startDateTimeAfter.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeBefore", endDateTimeBefore.format(DateTimeFormatter.ISO_DATE_TIME))
                        .param("endDateTimeAfter", endDateTimeAfter.format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.station_maintenance_list").isArray())
                .andExpect(jsonPath("$.page_info.page").value(0))
                .andExpect(jsonPath("$.page_info.limit").doesNotExist());

        verify(stationMaintenanceService).getAggregatedStationMaintenances(
                eq(startDateTimeBefore), eq(startDateTimeAfter), eq(endDateTimeBefore), eq(endDateTimeAfter));
    }

    @Test
    void getAggregatedStationMaintenancesWithoutFiltersSerializesAffectedInstitutions() throws Exception {
        OffsetDateTime start = OffsetDateTime.parse("2026-03-01T10:00:00+01:00");
        OffsetDateTime end = start.plusHours(1);
        when(stationMaintenanceService.getAggregatedStationMaintenances(null, null, null, null))
                .thenReturn(AggregatedStationMaintenanceListResource.builder()
                        .maintenanceList(Collections.singletonList(AggregatedStationMaintenanceResource.builder()
                                .brokerCode(BROKER_CODE)
                                .brokerName("Broker Name")
                                .startDateTime(start)
                                .endDateTime(end)
                                .stations(Collections.singletonList(STATION_CODE))
                                .creditorInstitutions(Collections.singletonList(AffectedCreditorInstitutionResource.builder()
                                        .businessName("Creditor Institution")
                                        .creditorInstitutionCode("12345678901")
                                        .build()))
                                .build()))
                        .pageInfo(PageInfo.builder().page(0).itemsFound(1).totalItems(1L).totalPages(1).build())
                        .build());

        mockMvc.perform(get("/brokers/station-maintenances/aggregated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station_maintenance_list[0].broker_code").value(BROKER_CODE))
                .andExpect(jsonPath("$.station_maintenance_list[0].broker_name").value("Broker Name"))
                .andExpect(jsonPath("$.station_maintenance_list[0].start_date_time").value("2026-03-01T10:00:00.000+01:00"))
                .andExpect(jsonPath("$.station_maintenance_list[0].end_date_time").value("2026-03-01T11:00:00.000+01:00"))
                .andExpect(jsonPath("$.station_maintenance_list[0].stations[0]").value(STATION_CODE))
                .andExpect(jsonPath("$.station_maintenance_list[0].creditor_institutions[0].business_name")
                        .value("Creditor Institution"))
                .andExpect(jsonPath("$.station_maintenance_list[0].creditor_institutions[0].creditor_institution_code")
                        .value("12345678901"))
                .andExpect(jsonPath("$.page_info.total_items").value(1));

        verify(stationMaintenanceService).getAggregatedStationMaintenances(null, null, null, null);
    }

    @Test
    void getBrokerMaintenancesSummaryTest() throws Exception {
        when(stationMaintenanceService.getBrokerMaintenancesSummary(anyString(), anyString()))
                .thenReturn(MaintenanceHoursSummaryResource.builder()
                        .usedHours("2")
                        .scheduledHours("3")
                        .remainingHours("31")
                        .extraHours("0")
                        .annualHoursLimit("36")
                        .build());

        mockMvc.perform(get("/brokers/{brokercode}/station-maintenances/summary", BROKER_CODE)
                        .param("maintenanceYear", "2024")
                ).andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteStationMaintenanceTest() throws Exception {
        mockMvc.perform(delete("/brokers/{brokercode}/station-maintenances/{maintenanceid}", BROKER_CODE, MAINTENANCE_ID))
                .andExpect(status().is2xxSuccessful());

        verify(stationMaintenanceService).deleteStationMaintenance(BROKER_CODE, MAINTENANCE_ID);
    }

    private StationMaintenanceResource buildMaintenanceResource() {
        return StationMaintenanceResource.builder()
                .maintenanceId(123L)
                .stationCode(STATION_CODE)
                .startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now())
                .standIn(true)
                .brokerCode(BROKER_CODE)
                .build();
    }

    private CreateStationMaintenance buildCreateStationMaintenance() {
        return CreateStationMaintenance.builder()
                .stationCode(STATION_CODE)
                .startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now())
                .standIn(true)
                .build();
    }

    private UpdateStationMaintenance buildUpdateStationMaintenance() {
        return UpdateStationMaintenance.builder()
                .startDateTime(OffsetDateTime.now())
                .endDateTime(OffsetDateTime.now())
                .standIn(true)
                .build();
    }

    private PageInfo buildPageInfo() {
        return PageInfo.builder()
                .totalPages(1)
                .page(0)
                .limit(10)
                .totalItems(1L)
                .itemsFound(1)
                .build();
    }
}
