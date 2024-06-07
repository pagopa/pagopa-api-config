package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.config.MappingsConfiguration;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionList;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStationList;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutions;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionsView;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ibans;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterPaView;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.CodificheRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanValidiPerPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.getCreditorInstitutionStationEdit;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCodifiche;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCodifichePa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCreditorInstitutionDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockFilterAndOrder;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanValidiPerPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CreditorInstitutionsService.class, MappingsConfiguration.class})
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
    private CreditorInstitutionsService creditorInstitutionsService;

    @MockBean
    private CodificheRepository codificheRepository;

    @MockBean
    private CodifichePaRepository codifichePaRepository;

    @Test
    void getECs_empty() throws IOException, JSONException {
        Page<Pa> page = TestUtil.mockPage(Collections.emptyList(), 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result =
                creditorInstitutionsService.getCreditorInstitutions(
                        50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_empty.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok1() throws IOException, JSONException {
        List<Pa> content = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(content, 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result =
                creditorInstitutionsService.getCreditorInstitutions(
                        50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok2() throws IOException, JSONException {
        List<Pa> ts = Lists.newArrayList(getMockPa());
        Page<Pa> page = TestUtil.mockPage(ts, 50, 0);
        when(paRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result =
                creditorInstitutionsService.getCreditorInstitutions(
                        50, 0, getMockFilterAndOrder(Order.CreditorInstitution.CODE));
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
        when(codificheRepository.findByIdCodifica(Encoding.CodeTypeEnum.QR_CODE.getValue()))
                .thenReturn(Optional.ofNullable(getMockCodifichePa().getFkCodifica()));

        ArgumentCaptor<CodifichePa> codifichePa = ArgumentCaptor.forClass(CodifichePa.class);

        CreditorInstitutionDetails result =
                creditorInstitutionsService.createCreditorInstitution(getMockCreditorInstitutionDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);

        verify(codifichePaRepository, times(1)).save(codifichePa.capture());
        assertEquals(getMockPa().getIdDominio(), codifichePa.getValue().getCodicePa());
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
        CreditorInstitutionDetails result =
                creditorInstitutionsService.updateCreditorInstitution("1234", mockCI);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateCreditorInstitution_notFound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());
        when(paRepository.save(any(Pa.class))).thenReturn(getMockPa());
        try {
            creditorInstitutionsService.updateCreditorInstitution(
                    "1234", getMockCreditorInstitutionDetails());
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
        when(paStazionePaRepository.findAllByFkPa(anyLong()))
                .thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        CreditorInstitutionStationList result =
                creditorInstitutionsService.getCreditorInstitutionStations("1234");
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile("response/get_creditorinstitution_stations_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(1L);
        mock.setSegregationCode(5L);
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/create_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3_blank(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        if (auxDigit == 0L) {
            mock.setApplicationCode(1L);
            mock.setSegregationCode(null);
        } else {
            mock.setApplicationCode(null);
            mock.setSegregationCode(5L);
        }
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/create_creditorinstitution_stations_ok_aux_digit_%s_blank.json",
                                auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void createStationsCI_1_2(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(null);
        mock.setSegregationCode(null);
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.createCreditorInstitutionStation("1234", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/create_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void createStationsCI_0_3_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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

    @Test
    void createStationsCI_conflict() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation(
                    "1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createStationsCI_2() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));

        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        PaStazionePa mock = getMockPaStazionePa();
        mock.setAuxDigit(1L);
        mock.setProgressivo(null);
        mock.setSegregazione(null);
        when(paStazionePaRepository.findAllByFkPaAndSegregazioneAndFkStazione_IdStazioneIsNot(
                anyLong(), anyLong(), anyString()))
                .thenReturn(Lists.newArrayList(mock));

        CreditorInstitutionStationEdit creditorInstitutionStationEdit =
                getCreditorInstitutionStationEdit();
        creditorInstitutionStationEdit.setApplicationCode(null);
        creditorInstitutionStationEdit.setSegregationCode(null);
        creditorInstitutionStationEdit.setAuxDigit(1L);
        var result =
                creditorInstitutionsService.createCreditorInstitutionStation(
                        "1234", creditorInstitutionStationEdit);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_creditorinstitution_ok_2.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createStationsCI_uniqueViolation() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndSegregazione(anyLong(), anyLong()))
                .thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation(
                    "1234", getCreditorInstitutionStationEdit());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void createStationsCI_uniqueViolation2() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndProgressivo(anyLong(), anyLong()))
                .thenReturn(Lists.newArrayList(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.createCreditorInstitutionStation(
                    "1234", getCreditorInstitutionStationEdit());
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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(1L);
        mock.setSegregationCode(5L);
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.updateCreditorInstitutionStation(
                        "1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/update_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3_blank(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        if (auxDigit == 0L) {
            mock.setApplicationCode(1L);
            mock.setSegregationCode(null);
        } else {
            mock.setApplicationCode(null);
            mock.setSegregationCode(5L);
        }
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.updateCreditorInstitutionStation(
                        "1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/update_creditorinstitution_stations_ok_aux_digit_%s_blank.json",
                                auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void updateStationsCI_1_2(Long auxDigit) throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(null);
        mock.setSegregationCode(null);
        CreditorInstitutionStationEdit result =
                creditorInstitutionsService.updateCreditorInstitutionStation(
                        "1234", "80007580279_01", mock);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile(
                        String.format(
                                "response/update_creditorinstitution_stations_ok_aux_digit_%s.json", auxDigit));
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

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
    @ValueSource(longs = {0L, 3L})
    void updateStationsCI_0_3_conflict(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        PaStazionePa paStazionePa = getMockPaStazionePa();
        paStazionePa.setObjId(333L);
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(paStazionePa));
        paStazionePa = getMockPaStazionePa();
        paStazionePa.setObjId(555L);
        when(paStazionePaRepository.findAllByFkPaAndSegregazione(anyLong(), anyLong()))
                .thenReturn(Lists.newArrayList(paStazionePa));
        when(paStazionePaRepository.findAllByFkPaAndProgressivo(anyLong(), anyLong()))
                .thenReturn(Lists.newArrayList(paStazionePa));

        CreditorInstitutionStationEdit mock = getCreditorInstitutionStationEdit();
        mock.setAuxDigit(auxDigit);
        mock.setApplicationCode(1L);
        mock.setSegregationCode(5L);
        try {
            creditorInstitutionsService.updateCreditorInstitutionStation("1234", "80007580279_01", mock);
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L})
    void updateStationsCI_1_2_badrequest(Long auxDigit) {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

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
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            creditorInstitutionsService.deleteCreditorInstitutionStation("1234", "80007580279_01");
        } catch (Exception e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void deleteStationsCI_notfound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione("80007580279_01"))
                .thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(ibanValidiPerPaRepository.findAllByFkPa(anyLong()))
                .thenReturn(Lists.newArrayList(getMockIbanValidiPerPa()));

        Ibans result = creditorInstitutionsService.getCreditorInstitutionsIbans("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCreditorInstitutionsByIban_1() throws IOException, JSONException {
        when(ibanValidiPerPaRepository.findAllByIbanAccreditoContainsIgnoreCase(anyString()))
                .thenReturn(Lists.newArrayList(getMockIbanValidiPerPa()));
        when(paRepository.findById(any())).thenReturn(Optional.of(getMockPa()));

        CreditorInstitutionList result =
                creditorInstitutionsService.getCreditorInstitutionsByIban("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_by_iban_1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCreditorInstitutionsByIban_2() throws IOException, JSONException {
        when(ibanValidiPerPaRepository.findAllByIbanAccreditoContainsIgnoreCase(anyString()))
                .thenReturn(Lists.newArrayList());

        CreditorInstitutionList result =
                creditorInstitutionsService.getCreditorInstitutionsByIban("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_by_iban_2.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCreditorInstitutionByPostalEncoding() throws IOException, JSONException {
        CodifichePa mockCodifichePa = getMockCodifichePa();
        Codifiche mockCodifiche = getMockCodifiche();
        mockCodifiche.setIdCodifica(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue());
        mockCodifichePa.setFkCodifica(mockCodifiche);
        mockCodifichePa.setFkPa(getMockPa());
        when(codifichePaRepository.findAllByCodicePaAndFkCodifica_IdCodifica(anyString(), anyString()))
                .thenReturn(Lists.newArrayList(mockCodifichePa));

        CreditorInstitutionList result =
                creditorInstitutionsService.getCreditorInstitutionByPostalEncoding("123456789012");
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile("response/get_creditorinstitutions_by_encoding.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCreditorInstitutionsServiceView() throws IOException, JSONException {
        Page<PaStazionePa> page = TestUtil.mockPage(Lists.newArrayList(getMockPaStazionePa()), 50, 0);
        when(paStazionePaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        CreditorInstitutionsView result =
                creditorInstitutionsService.getCreditorInstitutionsView(
                        50, 0, FilterPaView.builder().build());
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile("response/get_creditorinstitutionserviceview_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCreditorInstitutionsServiceWithFilters() throws IOException, JSONException {
        Page<PaStazionePa> page = TestUtil.mockPage(Lists.newArrayList(getMockPaStazionePa()), 50, 0);
        when(paStazionePaRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        FilterPaView filter =
                FilterPaView.builder()
                        .applicationCode(2L)
                        .auxDigit(1L)
                        .mod4(true)
                        .segregationCode(3L)
                        .creditorInstitutionCode("00168480242")
                        .stationCode("80007580279_01")
                        .paBrokerCode("1234")
                        .build();

        CreditorInstitutionsView result =
                creditorInstitutionsService.getCreditorInstitutionsView(50, 0, filter);
        String actual = TestUtil.toJson(result);
        String expected =
                TestUtil.readJsonFromFile("response/get_creditorinstitutionserviceview_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void loadCbillCsv(boolean incremental) throws IOException {
        File cbillCsv = TestUtil.readFile("file/massiveCbillValid_Insert.csv");
        MockMultipartFile file =
                new MockMultipartFile(
                        "file", cbillCsv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(cbillCsv));
        Pa pa = TestUtil.getMockPa();
        pa.setCbill(null);
        List<Pa> pas = List.of(pa);
        when(paRepository.findPaWithoutCbill())
                .thenReturn(Optional.of(pas));
        when(paRepository.findAll()).thenReturn(pas);

        assertEquals(null, pa.getCbill());
        creditorInstitutionsService.loadCbillByCsv(file, incremental);
        assertEquals("BFJ9Q", pa.getCbill());
    }
}
