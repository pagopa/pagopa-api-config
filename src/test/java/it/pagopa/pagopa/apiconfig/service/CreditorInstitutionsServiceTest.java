package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Ibans;
import it.pagopa.pagopa.apiconfig.model.StationCIList;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCodifichePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIbanValidiPerPa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CreditorInstitutionsServiceTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @MockBean
    private CodifichePaRepository codifichePaRepository;

    @MockBean
    private IbanValidiPerPaRepository ibanValidiPerPaRepository;

    @Autowired
    @InjectMocks
    private CreditorInstitutionsService creditorInstitutionsService;


    @Test
    void getECs_empty() throws IOException, JSONException {
        Page<Pa> page = TestUtil.mockPage(Lists.emptyList(), 50, 0);
        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_empty.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getECs_ok1() throws IOException, JSONException {
        List<Pa> content = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(content, 50, 0);
        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok2() throws IOException, JSONException {
        List<Pa> ts = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(ts, 50, 0);
        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok2.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getEC_ok() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));

        CreditorInstitutionDetails result = creditorInstitutionsService.getCreditorInstitution("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getEC_notFound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());
        try {
            creditorInstitutionsService.getCreditorInstitution("1234");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getStationsCI() throws IOException, JSONException {
        when(paStazionePaRepository.findAllFilterByPa(anyString())).thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        StationCIList result = creditorInstitutionsService.getStationsCI("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getCreditorInstitutionEncodings() throws IOException, JSONException {
        when(codifichePaRepository.findAllByCodicePa(anyString())).thenReturn(Lists.newArrayList(getMockCodifichePa()));

        CreditorInstitutionEncodings result = creditorInstitutionsService.getCreditorInstitutionEncodings("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_encondings.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getCreditorInstitutionIbans() throws IOException, JSONException {
        when(ibanValidiPerPaRepository.findAllByIdDominio(anyString())).thenReturn(Lists.newArrayList(getMockIbanValidiPerPa()));

        Ibans result = creditorInstitutionsService.getCreditorInstitutionsIbans("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans.json");
        System.out.println(actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}
