package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
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
        List<Pa> ts = Lists.emptyList();
        Page<Pa> page = Mockito.mock(Page.class);
        when(page.getTotalPages()).thenReturn(0);
        when(page.getNumberOfElements()).thenReturn(0);
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(0);
        when(page.stream()).thenReturn(ts.stream());

        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_empty.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok1() throws IOException, JSONException {
        List<Pa> ts = Lists.newArrayList(Pa.builder()
                .idDominio("00168480242")
                .enabled("Y")
                .ragioneSociale("Comune di Bassano del Grappa")
                .pagamentoPressoPsp("Y")
                .rendicontazioneFtp("N")
                .rendicontazioneZip("N")
                .build());
        Page<Pa> page = Mockito.mock(Page.class);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumberOfElements()).thenReturn(1);
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(1);
        when(page.stream()).thenReturn(ts.stream());

        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getECs_ok2() throws IOException, JSONException {
        List<Pa> ts = Lists.newArrayList(getMockPa());
        Page<Pa> page = Mockito.mock(Page.class);
        when(page.getTotalPages()).thenReturn(1);
        when(page.getNumberOfElements()).thenReturn(1);
        when(page.getNumber()).thenReturn(0);
        when(page.getSize()).thenReturn(1);
        when(page.stream()).thenReturn(ts.stream());
        when(paRepository.findAll(any(Pageable.class))).thenReturn(page);

        CreditorInstitutions result = creditorInstitutionsService.getCreditorInstitutions(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitutions_ok2.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getEC_ok() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPa(1L)).thenReturn(Lists.newArrayList(getMockPaStazionePa()));
        CreditorInstitutionDetails result = creditorInstitutionsService.getCreditorInstitution("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_ok.json");
        System.out.println(actual);
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