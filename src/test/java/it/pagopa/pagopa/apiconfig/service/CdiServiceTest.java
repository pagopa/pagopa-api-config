package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.model.psp.Cdis;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterRepository;
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

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCdiMaster;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CdiServiceTest {

    @MockBean
    private CdiMasterRepository cdiMasterRepository;

    @Autowired
    @InjectMocks
    private CdiService cdiService;


    @Test
    void getCdis() throws IOException, JSONException {
        Page<CdiMaster> page = TestUtil.mockPage(Lists.newArrayList(getMockCdiMaster()), 50, 0);
        when(cdiMasterRepository.findAll(any(Pageable.class))).thenReturn(page);

        Cdis result = cdiService.getCdis(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_cdis_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCdi() {
        when(cdiMasterRepository.findByIdInformativaPsp("1234")).thenReturn(Optional.of(getMockCdiMaster()));

        byte[] result  = cdiService.getCdi("1234");
        assertNotNull(result);
        assertEquals(2, result.length);
    }
}
