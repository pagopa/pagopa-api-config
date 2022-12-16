package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Pdd;
import it.pagopa.pagopa.apiconfig.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.AfmMarketplacePaymentType;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServer;
import it.pagopa.pagopa.apiconfig.model.configuration.FtpServers;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentTypes;
import it.pagopa.pagopa.apiconfig.model.configuration.Pdds;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
import it.pagopa.pagopa.apiconfig.repository.FtpServersRepository;
import it.pagopa.pagopa.apiconfig.repository.PddRepository;
import it.pagopa.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockAfmMarketplacePaymentType;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKey;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeyEntity;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockConfigurationKeysEntries;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFtpServer;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFtpServersEntities;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockFtpServersEntity;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockModelWfespPluginConf;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaymentType;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPdd;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPddEntity;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPddsEntities;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockTipiVersamento;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockTipoVersamento;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockWfespPluginConf;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockWfespPluginConfEntries;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class ConfigurationServiceTest {

    @MockBean
    private ConfigurationKeysRepository configurationKeysRepository;

    @MockBean
    private WfespPluginConfRepository wfespPluginConfRepository;

    @MockBean
    private PddRepository pddRepository;

    @MockBean
    private FtpServersRepository ftpServersRepository;

    @MockBean
    private TipiVersamentoRepository tipiVersamentoRepository;

    @Autowired
    @InjectMocks
    private ConfigurationService configurationService;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void getConfigurationKeys_ok() throws IOException, JSONException {
        List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKeyEntityList = getMockConfigurationKeysEntries();
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
    void deleteConfigurationKey() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.of(getMockConfigurationKeyEntity()));

        try {
            configurationService.deleteConfigurationKey("category", "key");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteConfigurationKey_notfound() {
        when(configurationKeysRepository.findByConfigCategoryAndConfigKey("category", "key")).thenReturn(Optional.empty());

        try {
            configurationService.deleteConfigurationKey("category", "key");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getWfespPlugins_ok() throws IOException, JSONException {
        List<WfespPluginConf> entries = getMockWfespPluginConfEntries();
        when(wfespPluginConfRepository.findAll()).thenReturn(entries);

        WfespPluginConfs wpList = configurationService.getWfespPluginConfigurations();
        String actual = TestUtil.toJson(wpList);
        String expected = TestUtil.readJsonFromFile("response/get_wfesp_plugins_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getWfespPlugin_ok() throws IOException, JSONException {
        WfespPluginConf wfespPluginConf = getMockWfespPluginConf();
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(java.util.Optional.ofNullable(wfespPluginConf));

        it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf wp = configurationService.getWfespPluginConfiguration("idServPlugin");
        String actual = TestUtil.toJson(wp);
        String expected = TestUtil.readJsonFromFile("response/get_wfesp_plugin_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getWfespPlugin_notFound() {
        when(wfespPluginConfRepository.findByIdServPlugin("unknown")).thenReturn(Optional.empty());
        try {
            configurationService.getWfespPluginConfiguration("unknown");
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
    void createWfespPlugin() throws IOException, JSONException {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.empty());
        when(wfespPluginConfRepository.save(any(it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class))).thenReturn(getMockWfespPluginConf());

        it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf result = configurationService.createWfespPluginConfiguration(getMockModelWfespPluginConf());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_wfesp_plugin_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createWfespPlugin_conflict() {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.of(getMockWfespPluginConf()));
        when(wfespPluginConfRepository.save(any(it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class))).thenReturn(getMockWfespPluginConf());

        try {
            configurationService.createWfespPluginConfiguration(getMockModelWfespPluginConf());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateWfespPlugin() throws IOException, JSONException {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.of(getMockWfespPluginConf()));
        when(wfespPluginConfRepository.save(any(it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class))).thenReturn(getMockWfespPluginConf());

        it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf result = configurationService.updateWfespPluginConfiguration("idServPlugin", getMockModelWfespPluginConf());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_wfesp_plugin_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateWfespPlugin_notFound() {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.empty());
        when(wfespPluginConfRepository.save(any(it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class))).thenReturn(getMockWfespPluginConf());

        try {
            configurationService.updateWfespPluginConfiguration("idServPlugin", getMockModelWfespPluginConf());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateWfespPlugin_badRequest() {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.of(getMockWfespPluginConf()));
        when(wfespPluginConfRepository.save(any(it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class))).thenReturn(getMockWfespPluginConf());

        try {
            it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf conf = getMockModelWfespPluginConf();
            conf.setIdServPlugin("");
            configurationService.updateWfespPluginConfiguration("idServPlugin", conf);
        }
        catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteWfespPlugin() {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.of(getMockWfespPluginConf()));

        try {
            configurationService.deleteWfespPluginConfiguration("idServPlugin");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteWfespPlugin_notfound() {
        when(wfespPluginConfRepository.findByIdServPlugin("idServPlugin")).thenReturn(Optional.empty());

        try {
            configurationService.deleteWfespPluginConfiguration("idServPlugin");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getPdds_ok() throws IOException, JSONException {
        List<it.pagopa.pagopa.apiconfig.entity.Pdd> pddList = getMockPddsEntities();
        when(pddRepository.findAll()).thenReturn(pddList);

        Pdds pdds = configurationService.getPdds();
        String actual = TestUtil.toJson(pdds);
        String expected = TestUtil.readJsonFromFile("response/get_pdds_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getPdd_ok() throws IOException, JSONException {
        Pdd pddEntity = getMockPddEntity();
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(java.util.Optional.ofNullable(pddEntity));

        it.pagopa.pagopa.apiconfig.model.configuration.Pdd pdd = configurationService.getPdd("idPdd");
        String actual = TestUtil.toJson(pdd);
        String expected = TestUtil.readJsonFromFile("response/get_pdd_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getPdd_notFound() {
        when(pddRepository.findByIdPdd("unknown")).thenReturn(java.util.Optional.empty());
        try {
            configurationService.getPdd("unknown");
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
    void createPdd() throws IOException, JSONException {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.empty());
        when(pddRepository.save(any(it.pagopa.pagopa.apiconfig.entity.Pdd.class))).thenReturn(getMockPddEntity());

        it.pagopa.pagopa.apiconfig.model.configuration.Pdd result = configurationService.createPdd(getMockPdd());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_pdd_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createPdd_conflict() {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(java.util.Optional.ofNullable( getMockPddEntity()));
        when(pddRepository.save(any(it.pagopa.pagopa.apiconfig.entity.Pdd.class))).thenReturn(getMockPddEntity());

        try {
            configurationService.createPdd(getMockPdd());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updatePdd() throws IOException, JSONException {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.of(getMockPddEntity()));
        when(pddRepository.save(any(it.pagopa.pagopa.apiconfig.entity.Pdd.class))).thenReturn(getMockPddEntity());

        it.pagopa.pagopa.apiconfig.model.configuration.Pdd result = configurationService.updatePdd("idPdd", getMockPdd());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_pdd_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updatePdd_notFound() {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.empty());
        when(pddRepository.save(any(it.pagopa.pagopa.apiconfig.entity.Pdd.class))).thenReturn(getMockPddEntity());

        try {
            configurationService.updatePdd("idPdd", getMockPdd());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updatePdd_badRequest() {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.of(getMockPddEntity()));
        when(pddRepository.save(any(it.pagopa.pagopa.apiconfig.entity.Pdd.class))).thenReturn(getMockPddEntity());

        try {
            it.pagopa.pagopa.apiconfig.model.configuration.Pdd conf = getMockPdd();
            conf.setIdPdd("");
            configurationService.updatePdd("idPdd", conf);
        }
        catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePdd() {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.of(getMockPddEntity()));

        try {
            configurationService.deletePdd("idPdd");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePdd_notfound() {
        when(pddRepository.findByIdPdd("idPdd")).thenReturn(Optional.empty());

        try {
            configurationService.deletePdd("idPdd");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getFtpServers_ok() throws IOException, JSONException {
        when(ftpServersRepository.findAll()).thenReturn(getMockFtpServersEntities());

        FtpServers ftpServers = configurationService.getFtpServers();
        String actual = TestUtil.toJson(ftpServers);
        String expected = TestUtil.readJsonFromFile("response/get_ftp_servers_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getFtpServer_ok() throws IOException, JSONException {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(java.util.Optional.ofNullable(getMockFtpServersEntity()));

        FtpServer ftpServer = configurationService.getFtpServer("host", 1, "service");
        String actual = TestUtil.toJson(ftpServer);
        String expected = TestUtil.readJsonFromFile("response/get_ftp_server_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getFtpServer_notFound() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(java.util.Optional.empty());
        try {
            configurationService.getFtpServer("unknown", 1, "unknown");
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
    void createFtpServer() throws IOException, JSONException {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(java.util.Optional.empty());
        when(ftpServersRepository.save(any(it.pagopa.pagopa.apiconfig.entity.FtpServers.class))).thenReturn(getMockFtpServersEntity());

        FtpServer result = configurationService.createFtpServer(getMockFtpServer());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_ftp_server_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createFtpServer_conflict() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(Optional.of(getMockFtpServersEntity()));
        when(ftpServersRepository.save(any(it.pagopa.pagopa.apiconfig.entity.FtpServers.class))).thenReturn(getMockFtpServersEntity());

        try {
            configurationService.createFtpServer(getMockFtpServer());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateFtpServer() throws IOException, JSONException {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(Optional.of(getMockFtpServersEntity()));
        when(ftpServersRepository.save(any(it.pagopa.pagopa.apiconfig.entity.FtpServers.class))).thenReturn(getMockFtpServersEntity());

        FtpServer result = configurationService.updateFtpServer("host", 1,"service", getMockFtpServer());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_ftp_server_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updateFtpServer_notFound() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(java.util.Optional.empty());
        when(ftpServersRepository.save(any(it.pagopa.pagopa.apiconfig.entity.FtpServers.class))).thenReturn(getMockFtpServersEntity());

        try {
            configurationService.updateFtpServer("host", 1, "service", getMockFtpServer());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updateFtpServer_badRequest() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(Optional.of(getMockFtpServersEntity()));
        when(ftpServersRepository.save(any(it.pagopa.pagopa.apiconfig.entity.FtpServers.class))).thenReturn(getMockFtpServersEntity());

        try {
            FtpServer conf = getMockFtpServer();
            conf.setHost("");
            configurationService.updateFtpServer("host", 1, "service", conf);
        }
        catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteFtpServer() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(Optional.of(getMockFtpServersEntity()));

        try {
            configurationService.deleteFtpServer("host", 1, "service");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deleteFtpServer_notfound() {
        when(ftpServersRepository.findByHostAndPortAndService("host", 1, "service")).thenReturn(java.util.Optional.empty());

        try {
            configurationService.deleteFtpServer("host", 1, "service");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void getPaymentTypes_ok() throws IOException, JSONException {
        List<TipiVersamento> tipiVersamento = getMockTipiVersamento();
        when(tipiVersamentoRepository.findAll()).thenReturn(tipiVersamento);

        PaymentTypes PaymentTypes = configurationService.getPaymentTypes();
        String actual = TestUtil.toJson(PaymentTypes);
        String expected = TestUtil.readJsonFromFile("response/get_payment_types_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }


    @Test
    void getPaymentType_ok() throws IOException, JSONException {
        TipiVersamento tipoVersamento = getMockTipoVersamento();
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(java.util.Optional.ofNullable(tipoVersamento));

        PaymentType PaymentType = configurationService.getPaymentType("PPAL");
        String actual = TestUtil.toJson(PaymentType);
        String expected = TestUtil.readJsonFromFile("response/get_payment_type_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getPaymentType_notFound() {
        when(tipiVersamentoRepository.findByTipoVersamento("unknown")).thenReturn(Optional.empty());
        try {
            configurationService.getPaymentType("unknown");
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
    void createPaymentType() throws IOException, JSONException {
        when(tipiVersamentoRepository.findByTipoVersamento("code")).thenReturn(Optional.empty());
        when(tipiVersamentoRepository.save(any(TipiVersamento.class))).thenReturn(getMockTipoVersamento());

        PaymentType result = configurationService.createPaymentType(getMockPaymentType());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/create_payment_type_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void createPaymentType_conflict() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        when(tipiVersamentoRepository.save(any(TipiVersamento.class))).thenReturn(getMockTipoVersamento());

        try {
            PaymentType mock = getMockPaymentType();
            mock.setPaymentTypeCode("PPAL");
            configurationService.createPaymentType(mock);
        }
        catch (AppException e) {
            assertEquals(HttpStatus.CONFLICT, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void updatePaymentType() throws IOException, JSONException {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        when(tipiVersamentoRepository.save(any(TipiVersamento.class))).thenReturn(getMockTipoVersamento());

        PaymentType result = configurationService.updatePaymentType("PPAL", getMockPaymentType());
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/update_payment_type_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void updatePaymentType_notFound() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.empty());
        when(tipiVersamentoRepository.save(any(TipiVersamento.class))).thenReturn(getMockTipoVersamento());

        try {
            configurationService.updatePaymentType("PPAL", getMockPaymentType());
        }
        catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePaymentType() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        ResponseEntity<AfmMarketplacePaymentType> responseEntity = new ResponseEntity<AfmMarketplacePaymentType>(getMockAfmMarketplacePaymentType(), HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), ArgumentMatchers.<HttpEntity<AfmMarketplacePaymentType>>any(), ArgumentMatchers.<Class<AfmMarketplacePaymentType>>any())).thenReturn(responseEntity);

        try {
            configurationService.deletePaymentType("PPAL");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePaymentType_notfound() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.empty());

        try {
            configurationService.deletePaymentType("PPAL");
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePaymentType_ko_afm_not_reachable() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        ResponseEntity<AfmMarketplacePaymentType> responseEntity = new ResponseEntity<AfmMarketplacePaymentType>(getMockAfmMarketplacePaymentType(), HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), ArgumentMatchers.<HttpEntity<AfmMarketplacePaymentType>>any(), ArgumentMatchers.<Class<AfmMarketplacePaymentType>>any())).thenReturn(responseEntity);

        try {
            configurationService.deletePaymentType("PPAL");
        } catch (AppException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePaymentType_ko_afm_found() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        AfmMarketplacePaymentType response = getMockAfmMarketplacePaymentType();
        response.setUsed(true);
        ResponseEntity<AfmMarketplacePaymentType> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), ArgumentMatchers.<HttpEntity<AfmMarketplacePaymentType>>any(), ArgumentMatchers.<Class<AfmMarketplacePaymentType>>any()))
                .thenReturn(responseEntity);

        try {
            configurationService.deletePaymentType("PPAL");
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void deletePaymentType_ko_afm_null() {
        when(tipiVersamentoRepository.findByTipoVersamento("PPAL")).thenReturn(Optional.of(getMockTipoVersamento()));
        ResponseEntity<AfmMarketplacePaymentType> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), ArgumentMatchers.<HttpEntity<AfmMarketplacePaymentType>>any(), ArgumentMatchers.<Class<AfmMarketplacePaymentType>>any()))
                .thenReturn(responseEntity);

        try {
            configurationService.deletePaymentType("PPAL");
        } catch (AppException e) {
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }
    }

}
