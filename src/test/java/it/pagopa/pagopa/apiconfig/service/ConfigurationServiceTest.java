package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
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

import java.io.IOException;
import java.util.List;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeysEntities;
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

}
