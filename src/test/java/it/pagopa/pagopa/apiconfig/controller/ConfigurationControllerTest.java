package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.service.ConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        when(configurationService.getConfigurationKey("category", "key")).thenReturn(getMockConfigurationKey("category", "key"));
    }

    @ParameterizedTest
    @CsvSource({
            "/configuration/keys",
            "/configuration/keys/key"

    })
    void testGets(String url) throws Exception {
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    void createConfigurationKey() throws Exception {
        mvc.perform(post("/configuration/keys")
                .content(TestUtil.toJson(getMockConfigurationKey()))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void createConfigurationKey_400() throws Exception {
        mvc.perform(post("/configuration/keys")
                .content(TestUtil.toJson(getMockConfigurationKey().toBuilder()
                        .configCategory("")
                        .configKey("")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateConfigurationKey() throws Exception {
        mvc.perform(put("/configuration/keys/category/category/key/key")
                .content(TestUtil.toJson(getMockConfigurationKey()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateConfigurationKey_400() throws Exception {
        mvc.perform(put("/configuration/keys/category/category/key/key")
                .content(
                        TestUtil.toJson(
                                getMockConfigurationKey().toBuilder()
                                        .configCategory("")
                                        .configKey("")
                                        .build())
                ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteConfigurationKey() throws Exception {
        mvc.perform(delete("/configuration/keys/category/category/key/key").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
