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
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
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
        Page<Pa> page = TestUtil.mockPage(Collections.emptyList(), 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_empty.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getECs_ok1() throws IOException, JSONException {
        List<Pa> content = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(content, 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok2() throws IOException, JSONException {
        List<Pa> ts = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(ts, 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
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

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {""})
    void updateCreditorInstitution(String zipCode) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        Pa mockPa = getMockPa();
        mockPa.setComuneDomicilioFiscale("");
        mockPa.setSiglaProvinciaDomicilioFiscale("");
        mockPa.setCapDomicilioFiscale(null);
        mockPa.setIndirizzoDomicilioFiscale("");
        when(paRepository.save(any(Pa.class))).thenReturn(mockPa);

        CreditorInstitutionDetails mockCI = getMockCreditorInstitutionDetails();
        mockCI.getAddress().setCity("");
        mockCI.getAddress().setCountryCode("");
        mockCI.getAddress().setZipCode(zipCode);
        mockCI.getAddress().setLocation("");
        CreditorInstitutionDetails result = creditorInstitutionsService.updateCreditorInstitution("1234", mockCI);
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
        when(paStazionePaRepository.findAllByFkPa(anyLong())).thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        CreditorInstitutionStationList result = creditorInstitutionsService.getCreditorInstitutionStations("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(1L);
        mock.setSegregationCode(5L);
        CreditorInstitutionStationEdit result = creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/create_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3_blank(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        if (auxDigit == 0L) {
            mock.setApplicationCode(1L);
            mock.setSegregationCode(null);
        }
        else {
            mock.setApplicationCode(null);
            mock.setSegregationCode(5L);
        }
        CreditorInstitutionStationEdit result = creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/create_creditorinstitution_stations_ok_aux_digit_%s_blank.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void createStationsCI_1_2(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(null);
        mock.setSegregationCode(null);
        CreditorInstitutionStationEdit result = creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/create_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(123L);
        mock.setSegregationCode(123L);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {123L})
    void createStationsCI_0_badrequest(Long code) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(0L);
        mock.setApplicationCode(code);
        mock.setSegregationCode(code);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void createStationsCI_1_2_t1_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(12L);
        mock.setSegregationCode(null);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void createStationsCI_1_2_t2_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(null);
        mock.setSegregationCode(12L);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {123L})
    void createStationsCI_3_badrequest(Long code) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(3L);
        mock.setApplicationCode(code);
        mock.setSegregationCode(code);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {4L})
    void createStationsCI_4_null_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(12L);
        mock.setSegregationCode(12L);
        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createStationsCI_conflict() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createStationsCI_uniqueViolation() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndSegregazione(anyLong(), anyLong())).thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createStationsCI_uniqueViolation2() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndProgressivo(anyLong(), anyLong())).thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation("1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(1L);
        mock.setSegregationCode(5L);
        CreditorInstitutionStationEdit result = creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/update_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3_blank(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        if (auxDigit == 0L) {
            mock.setApplicationCode(1L);
            mock.setSegregationCode(null);
        }
        else {
            mock.setApplicationCode(null);
            mock.setSegregationCode(5L);
        }
        CreditorInstitutionStationEdit result = creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/update_creditorinstitution_stations_ok_aux_digit_%s_blank.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void updateStationsCI_1_2(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(null);
        mock.setSegregationCode(null);
        CreditorInstitutionStationEdit result = creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile(String.format("response/update_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(123L);
        mock.setSegregationCode(123L);
        try {
            creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void updateStationsCI_1_2_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(12L);
        mock.setSegregationCode(12L);
        try {
            creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteStationsCI() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01")).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

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
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

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
