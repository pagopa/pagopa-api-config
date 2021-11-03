package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CounterpartTables;
import it.pagopa.pagopa.apiconfig.repository.InformativePaMasterRepository;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativePaMaster;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CounterpartServiceTest {

    @MockBean
    private InformativePaMasterRepository informativePaMasterRepository;

    @Autowired
    @InjectMocks
    private CounterpartService counterpartService;

    @Test
    void getCounterpartTables() throws IOException, JSONException {
        Page<InformativePaMaster> page = TestUtil.mockPage(Lists.newArrayList(getMockInformativePaMaster()), 50, 0);
        when(informativePaMasterRepository.findAll(any(Pageable.class))).thenReturn(page);

        CounterpartTables result = counterpartService.getCounterpartTables(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_counterparttables_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getCounterpartTable() {
        when(informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(anyString(), anyString())).thenReturn(Optional.of(getMockInformativePaMaster()));

        byte[] result = counterpartService.getCounterpartTable("111", "222");
        assertNotNull(result);
        assertEquals(2, result.length);
    }
}
