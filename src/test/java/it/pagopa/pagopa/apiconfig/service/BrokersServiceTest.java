package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
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
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBrokerDetails;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockIntermediariePa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getBroker() throws IOException, JSONException {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.of(getMockIntermediariePa()));

        BrokerDetails result = brokersService.getBroker("1234");
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_broker_ok1.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createBroker() throws IOException, JSONException {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.empty());
        when(intermediariPaRepository.save(any(IntermediariPa.class))).thenReturn(getMockIntermediariePa());

        BrokerDetails result = brokersService.createBroker(getMockBrokerDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_broker_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createBroker_conflict() {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.of(getMockIntermediariePa()));

        try {
            brokersService.createBroker(getMockBrokerDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateBroker() throws IOException, JSONException {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.of(getMockIntermediariePa()));
        when(intermediariPaRepository.save(any(IntermediariPa.class))).thenReturn(getMockIntermediariePa());

        BrokerDetails result = brokersService.updateBroker("1234", getMockBrokerDetails());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_broker_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateBroker_notFound() {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.empty());
        try {
            brokersService.updateBroker("1234", getMockBrokerDetails());
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteBroker() {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.of(getMockIntermediariePa()));

        brokersService.deleteBroker("1234");
        assertTrue(true);
    }

    @Test
    void deleteBroker_notfound() {
        when(intermediariPaRepository.findByIdIntermediarioPa("1234")).thenReturn(Optional.empty());

        try {
            brokersService.deleteBroker("1234");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

}
