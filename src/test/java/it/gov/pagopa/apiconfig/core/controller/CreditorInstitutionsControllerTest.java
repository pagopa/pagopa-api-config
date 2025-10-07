package it.gov.pagopa.apiconfig.core.controller;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterPaView;
import it.gov.pagopa.apiconfig.core.service.CreditorInstitutionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;

import static it.gov.pagopa.apiconfig.TestUtil.getCreditorInstitutionStationEdit;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionStationEdit;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionStationList;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutions;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionsView;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class CreditorInstitutionsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CreditorInstitutionsService creditorInstitutionsService;

    @BeforeEach
    void setUp() {
        when(creditorInstitutionsService.getCreditorInstitutions(
                anyInt(), anyInt(), any(FilterAndOrder.class), nullable(Boolean.class), nullable(Boolean.class)))
                .thenReturn(getMockCreditorInstitutions());
        when(creditorInstitutionsService.getCreditorInstitution("1234"))
                .thenReturn(getMockCreditorInstitutionDetails());
        when(creditorInstitutionsService.getCreditorInstitutionStations("1234"))
                .thenReturn(getMockCreditorInstitutionStationList());
        when(creditorInstitutionsService.createCreditorInstitution(
                any(CreditorInstitutionDetails.class)))
                .thenReturn(getMockCreditorInstitutionDetails());
        when(creditorInstitutionsService.updateCreditorInstitution(
                anyString(), any(CreditorInstitutionDetails.class)))
                .thenReturn(getMockCreditorInstitutionDetails());
        when(creditorInstitutionsService.createCreditorInstitutionStation(
                anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(getMockCreditorInstitutionStationEdit());
        when(creditorInstitutionsService.updateCreditorInstitutionStation(
                anyString(), anyString(), any(CreditorInstitutionStationEdit.class)))
                .thenReturn(getMockCreditorInstitutionStationEdit());
        when(creditorInstitutionsService.getCreditorInstitutionsView(
                anyInt(), anyInt(), any(FilterPaView.class)))
                .thenReturn(getMockCreditorInstitutionsView());
    }

    @ParameterizedTest
    @CsvSource({
            "/creditorinstitutions?page=0",
            "/creditorinstitutions/1234",
            "/creditorinstitutions/1234/stations",
            "/creditorinstitutions/view?page=0",
            "/creditorinstitutions/view?page=0&creditorInstitutionCode=00168480242"
    })
    void testGets(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCreditorInstitution() throws Exception {
        mvc.perform(
                        post("/creditorinstitutions")
                                .content(TestUtil.toJson(getMockCreditorInstitutionDetails()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCreditorInstitution_400() throws Exception {
        mvc.perform(
                        post("/creditorinstitutions")
                                .content(
                                        TestUtil.toJson(
                                                getMockCreditorInstitutionDetails().toBuilder()
                                                        .creditorInstitutionCode("")
                                                        .build()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCreditorInstitution() throws Exception {
        mvc.perform(
                        put("/creditorinstitutions/1234")
                                .content(TestUtil.toJson(getMockCreditorInstitutionDetails()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCreditorInstitution_400() throws Exception {
        mvc.perform(
                        put("/creditorinstitutions/1234")
                                .content(
                                        TestUtil.toJson(
                                                getMockCreditorInstitutionDetails().toBuilder()
                                                        .creditorInstitutionCode("")
                                                        .build()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCreditorInstitution() throws Exception {
        mvc.perform(delete("/creditorinstitutions/1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createCreditorInstitutionStation() throws Exception {
        mvc.perform(
                        post("/creditorinstitutions/123/stations")
                                .content(TestUtil.toJson(getCreditorInstitutionStationEdit()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCreditorInstitutionStation_BadRequest() throws Exception {
        var request = getCreditorInstitutionStationEdit();
        request.setAuxDigit(4L);
        mvc.perform(
                        post("/creditorinstitutions/123/stations")
                                .content(TestUtil.toJson(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCreditorInstitutionStation() throws Exception {
        mvc.perform(
                        put("/creditorinstitutions/1234/stations/21")
                                .content(TestUtil.toJson(getCreditorInstitutionStationEdit().toBuilder().build()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCreditorInstitutionStation_badRequest() throws Exception {
        mvc.perform(
                        put("/creditorinstitutions/1234/stations/21")
                                .content(
                                        TestUtil.toJson(
                                                getCreditorInstitutionStationEdit().toBuilder().stationCode("").build()))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void uploadMassiveCbill() throws Exception {
        File file = TestUtil.readFile("file/massiveCbillValid_Insert.csv");
        MockMultipartFile multipartFile =
                new MockMultipartFile(
                        "file", file.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(file));
        String url = "/creditorinstitutions/cbill";

        // test incremental load
        mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        // test full load
        mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA)
                        .queryParam("incremental", "false"))
                .andExpect(status().isOk());
    }
}
