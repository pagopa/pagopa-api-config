package it.gov.pagopa.apiconfig.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.core.model.stationmaintenance.CreateStationMaintenance;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
}