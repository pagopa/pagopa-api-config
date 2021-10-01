package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.model.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.Brokers;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIntermediariePa;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class BrokersServiceTest {

    @MockBean
    private IntermediariPaRepository intermediariPaRepository;

    @Autowired
    @InjectMocks
    private BrokersService brokersService;

    @Test
    void getBrokers() throws IOException, JSONException {
        Page<IntermediariPa> page = TestUtil.mockPage(Lists.newArrayList(getMockIntermediariePa()), 50, 0);
        when(intermediariPaRepository.findAll(any(Pageable.class))).thenReturn(page);

        Brokers result = brokersService.getBrokers(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_brokers_ok1.json");
        System.out.println(actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getBroker() throws IOException, JSONException {
        when(intermediariPaRepository.findByCodiceIntermediario("1234")).thenReturn(Optional.of(getMockIntermediariePa()));

        BrokerDetails result = brokersService.getBroker("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_broker_ok1.json");
        System.out.println(actual);
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

}
