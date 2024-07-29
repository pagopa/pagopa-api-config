package it.gov.pagopa.apiconfig.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
