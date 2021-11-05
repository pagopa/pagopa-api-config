package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ibans;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getCreditorInstitutionStationEdit;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIbanValidiPerPa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CreditorInstitutionsServiceTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @MockBean
    private IbanValidiPerPaRepository ibanValidiPerPaRepository;

    @MockBean
    private StazioniRepository stazioniRepository;

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
    void createCreditorInstitution() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());
        when(paRepository.save(any(Pa.class))).thenReturn(getMockPa());

        CreditorInstitutionDetails result = creditorInstitutionsService.createCreditorInstitution(getMockCreditorInstitutionDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createCreditorInstitution_conflict() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(paRepository.save(any(Pa.class))).thenReturn(getMockPa());

        try {
            creditorInstitutionsService.createCreditorInstitution(getMockCreditorInstitutionDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateCreditorInstitution() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(paRepository.save(any(Pa.class))).thenReturn(getMockPa());

        CreditorInstitutionDetails result = creditorInstitutionsService.updateCreditorInstitution("1234", getMockCreditorInstitutionDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateCreditorInstitution_notFound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());
        when(paRepository.save(any(Pa.class))).thenReturn(getMockPa());
        try {
            creditorInstitutionsService.updateCreditorInstitution("1234", getMockCreditorInstitutionDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteCreditorInstitution() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));

        try {
            creditorInstitutionsService.deleteCreditorInstitution("1234");
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void deleteCreditorInstitution_notfound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());

        try {
            creditorInstitutionsService.deleteCreditorInstitution("1234");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getStationsCI() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        CreditorInstitutionStationList result = creditorInstitutionsService.getCreditorInstitutionStations("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createStationsCI() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit result = creditorInstitutionsService.createCreditorInstitutionStation("1234", getCreditorInstitutionStationEdit());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createStationsCI_conflict() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateStationsCI() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit result = creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", getCreditorInstitutionStationEdit());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void deleteStationsCI() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.deleteCreditorInstitutionStation("1234", "80007580279_01");
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void deleteStationsCI_notfound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        try {
            creditorInstitutionsService.deleteCreditorInstitutionStation("1234", "80007580279_01");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getCreditorInstitutionIbans() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(ibanValidiPerPaRepository.findAllByFkPa(anyLong())).thenReturn(Lists.newArrayList(getMockIbanValidiPerPa()));

        Ibans result = creditorInstitutionsService.getCreditorInstitutionsIbans("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}
