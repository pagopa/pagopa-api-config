package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeyBase;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKey;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeys;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFtpServer;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFtpServers;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockModelWfespPluginConf;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentType;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentTypes;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPdd;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPdds;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockWfespPluginConfigurations;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class ConfigurationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {
        when(configurationService.getConfigurationKeys()).thenReturn(getMockConfigurationKeys());
        when(configurationService.getConfigurationKey("category", "key")).thenReturn(getMockConfigurationKey("category", "key"));

        when(configurationService.getWfespPluginConfigurations()).thenReturn(getMockWfespPluginConfigurations());
        when(configurationService.getWfespPluginConfiguration("idServPlugin")).thenReturn(getMockModelWfespPluginConf());

        when(configurationService.getPdds()).thenReturn(getMockPdds());
        when(configurationService.getPdd("idPdd")).thenReturn(getMockPdd());

        when(configurationService.getFtpServers()).thenReturn(getMockFtpServers());
        when(configurationService.getFtpServer("host", 1, "service")).thenReturn(getMockFtpServer());

        when(configurationService.getPaymentTypes()).thenReturn(getMockPaymentTypes());
        when(configurationService.getPaymentType("code")).thenReturn(getMockPaymentType());
    }

    @ParameterizedTest
    @CsvSource({
            "/configuration/keys",
            "/configuration/keys/category/category/key/key",
            "/configuration/paymenttypes/code",
            "/configuration/wfespplugins",
            "/configuration/wfespplugins/idServPlugin",
            "/configuration/pdds",
            "/configuration/pdds/idPdd",
            "/configuration/ftpservers",
            "/configuration/ftpservers/host/host/port/1/service/service",
            "/configuration/paymenttypes",
            "/configuration/paymenttypes/code"
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
        when(configurationService.updateConfigurationKey(anyString(), anyString(), any(ConfigurationKeyBase.class))).thenReturn(getMockConfigurationKey("category", "key"));

        mvc.perform(put("/configuration/keys/category/category/key/key")
                .content(TestUtil.toJson(getMockConfigurationKey()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteConfigurationKey() throws Exception {
        doNothing().when(configurationService).deleteConfigurationKey(anyString(), anyString());
        mvc.perform(delete("/configuration/keys/category/category/key/key").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createWfespPlugin() throws Exception {
        mvc.perform(post("/configuration/wfespplugins")
                .content(TestUtil.toJson(getMockModelWfespPluginConf()))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void createWfespPlugin_400() throws Exception {
        mvc.perform(post("/configuration/wfespplugins")
                .content(TestUtil.toJson(getMockModelWfespPluginConf().toBuilder()
                        .idServPlugin("")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateWfespPlugin() throws Exception {
        mvc.perform(put("/configuration/wfespplugins/idServPlugin")
                .content(TestUtil.toJson(getMockModelWfespPluginConf()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWfespPlugin() throws Exception {
        mvc.perform(delete("/configuration/wfespplugins/idServPlugin").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPdd() throws Exception {
        mvc.perform(post("/configuration/pdds")
                .content(TestUtil.toJson(getMockPdd()))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void createPdd_400() throws Exception {
        mvc.perform(post("/configuration/pdds")
                .content(TestUtil.toJson(getMockPdd().toBuilder()
                        .idPdd("")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updatePdd() throws Exception {
        mvc.perform(put("/configuration/pdds/idPdd")
                .content(TestUtil.toJson(getMockPdd()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletePd() throws Exception {
        mvc.perform(delete("/configuration/pdds/idPdd").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createFtpServer() throws Exception {
        mvc.perform(post("/configuration/ftpservers")
                .content(TestUtil.toJson(getMockFtpServer()))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }


    @Test
    void createFtpServer_400() throws Exception {
        mvc.perform(post("/configuration/ftpservers")
                .content(TestUtil.toJson(getMockFtpServer().toBuilder()
                        .host("")
                        .build()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateFtpServer() throws Exception {
        mvc.perform(put("/configuration/ftpservers/host/host/port/1/service/service")
                .content(TestUtil.toJson(getMockFtpServer()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateFtpServer_400() throws Exception {
        mvc.perform(put("/configuration/ftpservers/host/host/port/1/service/service")
                .content(
                        TestUtil.toJson(
                                getMockFtpServer().toBuilder()
                                        .host("")
                                        .build())
                ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteFtpServer() throws Exception {
        mvc.perform(delete("/configuration/ftpservers/host/host/port/1/service/service").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createPaymentType() throws Exception {
        mvc.perform(post("/configuration/paymenttypes")
                .content(TestUtil.toJson(getMockPaymentType()))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePaymentType() throws Exception {
        mvc.perform(put("/configuration/paymenttypes/ABCVDF")
                .content(TestUtil.toJson(getMockPaymentType()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deletePaymentType() throws Exception {
        mvc.perform(delete("/configuration/paymenttypes/ABCVDF").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
