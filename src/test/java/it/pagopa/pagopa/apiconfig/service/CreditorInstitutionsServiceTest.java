package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
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

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CreditorInstitutionsServiceTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    @InjectMocks
    private CreditorInstitutionsService creditorInstitutionsService;


    @Test
    void getECs_empty() throws IOException, JSONException {
        CreditorInstitutions result = creditorInstitutionsService.getECs();
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_empty.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok1() throws IOException, JSONException {
        when(paRepository.findAll()).thenReturn(Lists.newArrayList(Pa.builder()
                .idDominio("00168480242")
                .enabled("Y")
                .ragioneSociale("Comune di Bassano del Grappa")
                .pagamentoPressoPsp("Y")
                .rendicontazioneFtp("N")
                .rendicontazioneZip("N")
                .build()));

        CreditorInstitutions result = creditorInstitutionsService.getECs();
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok2() throws IOException, JSONException {
        when(paRepository.findAll()).thenReturn(Lists.newArrayList(getMockPa()));

        CreditorInstitutions result = creditorInstitutionsService.getECs();
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok2.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getEC_ok() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPa(1L)).thenReturn(Lists.newArrayList(getMockPaStazionePa()));
        CreditorInstitutionDetails result = creditorInstitutionsService.getEC("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    private Pa getMockPa() {
        return Pa.builder()
                .objId(1L)
                .idDominio("00168480242")
                .enabled("Y")
                .ragioneSociale("Comune di Bassano del Grappa")
                .capDomicilioFiscale(123L)
                .pagamentoPressoPsp("Y")
                .rendicontazioneFtp("N")
                .rendicontazioneZip("N")
                .build();
    }

    private PaStazionePa getMockPaStazionePa() {
        return PaStazionePa.builder()
                .fkPa(1L)
                .fkStazioni(Stazioni.builder().idStazione("1").build())
                .build();
    }
}