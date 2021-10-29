package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.CodificheRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCodifiche;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCodifichePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockEncoding;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class EncodingsServiceTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private CodifichePaRepository codifichePaRepository;

    @MockBean
    private CodificheRepository codificheRepository;

    @Autowired
    @InjectMocks
    private EncodingsService encodingsService;


    @Test
    void getCreditorInstitutionEncodings() throws IOException, JSONException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.newArrayList(getMockCodifichePa()));

        CreditorInstitutionEncodings result = encodingsService.getCreditorInstitutionEncodings("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_creditorinstitution_encondings.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createCreditorInstitutionEncoding() throws JSONException, IOException {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findByCodicePaAndFkPa_ObjId(anyString(), anyLong())).thenReturn(Optional.empty());
        when(codificheRepository.findByIdCodifica(anyString())).thenReturn(getMockCodifiche());
        when(codifichePaRepository.save(any(CodifichePa.class))).thenReturn(getMockCodifichePa());

        Encoding result = encodingsService.createCreditorInstitutionEncoding("1234", getMockEncoding());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_creditorinstitution_encoding_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createCreditorInstitutionEncoding_conflict() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findByCodicePaAndFkPa_ObjId(anyString(), anyLong())).thenReturn(Optional.of(getMockCodifichePa()));

        try {
            encodingsService.createCreditorInstitutionEncoding("1234", getMockEncoding());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteCreditorInstitutionEncoding() {
        try {
            when(paRepository.findByIdDominio("1234")).thenReturn(Optional.of(getMockPa()));
            when(codifichePaRepository.findByCodicePaAndFkPa_ObjId(anyString(), anyLong())).thenReturn(Optional.of(getMockCodifichePa()));

            encodingsService.deleteCreditorInstitutionEncoding("1234", "00011");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteCreditorInstitutionEncoding_notFound() {
        when(paRepository.findByIdDominio("1234")).thenReturn(Optional.empty());
        when(codifichePaRepository.findByCodicePaAndFkPa_ObjId(anyString(), anyLong())).thenReturn(Optional.of(getMockCodifichePa()));

        try {
            encodingsService.deleteCreditorInstitutionEncoding("1234", "00011");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }
}
