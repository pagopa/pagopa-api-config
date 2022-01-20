package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKey;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeys;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
public class ConfigurationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        when(configurationService.getConfigurationKeys()).thenReturn(getMockConfigurationKeys());
        when(configurationService.getConfigurationKey("key")).thenReturn(getMockConfigurationKey("key"));
    }

    @ParameterizedTest
    @CsvSource({
            "/configuration/keys",
            "/configuration/keys/key"

    })
    void testGets(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
