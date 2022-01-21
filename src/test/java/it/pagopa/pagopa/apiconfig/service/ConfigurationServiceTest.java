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

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
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
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(java.util.Optional.ofNullable(configKeyEntity));

        ConfigurationKey configurationKey = configurationService.getConfigurationKey("category", "key");
        String actual = TestUtil.toJson(configurationKey);
        String expected = TestUtil.readJsonFromFile("response/get_configuration_key_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getConfigurationKey_notFound() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("unknown", "unknown")).thenReturn(Optional.empty());
        try {
            configurationService.getConfigurationKey("unknown", "unknown");
            fail();
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void createConfigurationKey() throws IOException, JSONException {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.empty());
        when(configurationKeysRepository.save(any(it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class))).thenReturn(getMockConfigurationKeyEntity());

        ConfigurationKey result = configurationService.createConfigurationKey(getMockConfigurationKey("category", "key"));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_configuration_key_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createConfigurationKey_conflict() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.of(getMockConfigurationKeyEntity()));
        when(configurationKeysRepository.save(any(it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class))).thenReturn(getMockConfigurationKeyEntity());

        try {
            configurationService.createConfigurationKey(getMockConfigurationKey("category", "key"));
        }
        catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateConfigurationKey() throws IOException, JSONException {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.of(getMockConfigurationKeyEntity()));
        when(configurationKeysRepository.save(any(it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class))).thenReturn(getMockConfigurationKeyEntity());

        ConfigurationKey result = configurationService.updateConfigurationKey("category", "key", getMockConfigurationKey("category", "key"));
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_configuration_key_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateConfigurationKey_notFound() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.empty());
        when(configurationKeysRepository.save(any(it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class))).thenReturn(getMockConfigurationKeyEntity());

        try {
            configurationService.updateConfigurationKey("category", "key", getMockConfigurationKey("category", "key"));
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateConfigurationKey_badRequest() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.of(getMockConfigurationKeyEntity()));
        when(configurationKeysRepository.save(any(it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class))).thenReturn(getMockConfigurationKeyEntity());

        try {
            ConfigurationKey conf = getMockConfigurationKey("category", "key");
            conf.setConfigCategory("");
            conf.setConfigKey("");
            configurationService.updateConfigurationKey("category", "key", conf);
        }
        catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteConfigurationService() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.of(getMockConfigurationKeyEntity()));

        try {
            configurationService.deleteConfigurationKey("category", "key");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteConfigurationService_notfound() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.empty());

        try {
            configurationService.deleteConfigurationKey("category", "key");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

}
