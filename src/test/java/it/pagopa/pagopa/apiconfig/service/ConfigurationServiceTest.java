package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
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
import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeyEntity;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeysEntities;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
public class ConfigurationServiceTest {

    @MockBean
    private ConfigurationKeysRepository configurationKeysRepository;

    @Autowired
    @InjectMocks
    private ConfigurationService configurationService;

    @Test
    void getConfigurationKeys_ok() throws IOException, JSONException {
        List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKeyEntityList = getMockConfigurationKeysEntities();
        when(configurationKeysRepository.findAll()).thenReturn(configKeyEntityList);

        ConfigurationKeys configurationKeys = configurationService.getConfigurationKeys();
        String actual = TestUtil.toJson(configurationKeys);
        String expected = TestUtil.readJsonFromFile("response/get_configuration_keys_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getConfigurationKey_ok() throws IOException, JSONException {
        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configKeyEntity = getMockConfigurationKeyEntity();
        when(configurationKeysRepository.findByConfigKey("key")).thenReturn(java.util.Optional.ofNullable(configKeyEntity));

        ConfigurationKey configurationKey = configurationService.getConfigurationKey("key");
        String actual = TestUtil.toJson(configurationKey);
        String expected = TestUtil.readJsonFromFile("response/get_configuration_key_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getConfigurationKey_notFound() {
        when(configurationKeysRepository.findByConfigKey("unknown")).thenReturn(Optional.empty());
        try {
            configurationService.getConfigurationKey("unknown");
            fail();
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

}
