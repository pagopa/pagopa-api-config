package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.model.psp.BrokersPsp;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIntermediariePsp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
@ActiveProfiles(profiles = "azure-d")
class BrokersPspServiceTest {

    @MockBean
    private IntermediariPspRepository intermediariPspRepository;

    @Autowired
    @InjectMocks
    private BrokersPspService brokersPspService;

    @Test
    void getBrokers() throws IOException, JSONException {
        Page<IntermediariPsp> page = TestUtil.mockPage(Lists.newArrayList(getMockIntermediariePsp()), 50, 0);
        when(intermediariPspRepository.findAll(any(Pageable.class))).thenReturn(page);

        BrokersPsp result = brokersPspService.getBrokersPsp(50, 0);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_brokerspsp_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getBroker() throws IOException, JSONException {
        when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.of(getMockIntermediariePsp()));

        BrokerPspDetails result = brokersPspService.getBrokerPsp("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_brokerpsp_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getBroker_notFound() {
        when(intermediariPspRepository.findByIdIntermediarioPsp("1234")).thenReturn(Optional.empty());

        try {
            brokersPspService.getBrokerPsp("123");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }


}
