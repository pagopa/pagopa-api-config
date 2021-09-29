package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Ibans;
import it.pagopa.pagopa.apiconfig.model.StationCIList;
import it.pagopa.pagopa.apiconfig.service.CreditorInstitutionsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        when(creditorInstitutionsService.getCreditorInstitutions(50, 0)).thenReturn(CreditorInstitutions.builder().build());
        when(creditorInstitutionsService.getCreditorInstitution("1234")).thenReturn(CreditorInstitutionDetails.builder().build());
        when(creditorInstitutionsService.getStationsCI("1234")).thenReturn(StationCIList.builder().build());
        when(creditorInstitutionsService.getCreditorInstitutionEncodings("1234")).thenReturn(CreditorInstitutionEncodings.builder().build());
        when(creditorInstitutionsService.getCreditorInstitutionsIbans("1234")).thenReturn(Ibans.builder().build());
    }

    @ParameterizedTest
    @CsvSource({
            "/creditorinstitutions?page=0",
            "/creditorinstitutions/1234",
            "/creditorinstitutions/1234/stations",
            "/creditorinstitutions/1234/encodings",
            "/creditorinstitutions/1234/ibans",
    })
    void testGets(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));    }

}
