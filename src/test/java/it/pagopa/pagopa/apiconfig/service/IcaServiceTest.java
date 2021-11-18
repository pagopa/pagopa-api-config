package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
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
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativeContoAccreditoMaster;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class IcaServiceTest {

    @MockBean
    InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

    @Autowired
    @InjectMocks
    IcaService icaService;

    @Test
    void getIcas() throws JSONException, IOException {
        Page<InformativeContoAccreditoMaster> page = TestUtil.mockPage(Lists.newArrayList(getMockInformativeContoAccreditoMaster()), 50, 0);
        when(informativeContoAccreditoMasterRepository.findAll(any(Pageable.class))).thenReturn(page);

        Icas result = icaService.getIcas(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_icas_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getIca() {
        when(informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString())).thenReturn(Optional.of(getMockInformativeContoAccreditoMaster()));

        byte[] result = icaService.getIca("111", "1234");
        assertNotNull(result);
        assertEquals(2, result.length);
    }


    @Test
    void getEC_NotFound() {
        when(informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString())).thenReturn(Optional.empty());
        try {
            icaService.getIca("111", "1234");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

}
