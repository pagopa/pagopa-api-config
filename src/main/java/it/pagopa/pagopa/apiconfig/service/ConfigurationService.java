package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.*;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
import it.pagopa.pagopa.apiconfig.repository.PddRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationKeysRepository configurationKeysRepository;

    @Autowired
    private WfespPluginConfRepository wfespPluginConfRepository;

    @Autowired
    private PddRepository pddRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ConfigurationKeys getConfigurationKeys() {
        List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKeyList = configurationKeysRepository.findAll();
        return it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys.builder()
                .configurationKeyList(getConfigurationKeys(configKeyList))
                .build();
    }

    public ConfigurationKey getConfigurationKey(@NotNull String category, @NotNull String key) {
        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configKey = getConfigurationKeyIfExists(category, key);
        return modelMapper.map(configKey, ConfigurationKey.class);
    }

    public ConfigurationKey createConfigurationKey(ConfigurationKey configurationKey) {
        Optional<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKey = configurationKeysRepository.findByConfigCategoryAndConfigKey(configurationKey.getConfigCategory(), configurationKey.getConfigKey());
        if (configKey.isPresent()) {
            throw new AppException(AppError.CONFIGURATION_KEY_CONFLICT, configKey.get().getConfigCategory(), configKey.get().getConfigKey());
        }

        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configKeyEntity = modelMapper.map(configurationKey, it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys.class);
        configurationKeysRepository.save(configKeyEntity);
        return modelMapper.map(configKeyEntity, ConfigurationKey.class);
    }

    public ConfigurationKey updateConfigurationKey(String category, String key, ConfigurationKey configurationKey) {
        Optional<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKey = configurationKeysRepository.findByConfigCategoryAndConfigKey(category, key);
        if (configKey.isEmpty()) {
            throw new AppException(AppError.CONFIGURATION_KEY_NOT_FOUND, category, key);
        }

        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configKeyEntity = configKey.get();
        configKeyEntity.setConfigValue(configurationKey.getConfigValue());
        configKeyEntity.setConfigDescription(configurationKey.getConfigDescription());
        configurationKeysRepository.save(configKeyEntity);

        return modelMapper.map(configKeyEntity, ConfigurationKey.class);
    }

    public void deleteConfigurationKey(String category, String key) {
        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configurationKey = getConfigurationKeyIfExists(category, key);
        configurationKeysRepository.delete(configurationKey);
    }

    public WfespPluginConfs getWfespPluginConfigurations() {
        List<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> list = wfespPluginConfRepository.findAll();
        return it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs.builder()
                .wfespPluginConfList(getWfespPluginConfList(list))
                .build();
    }

    public WfespPluginConf getWfespPluginConfiguration(@NotNull String idServPlugin) {
        it.pagopa.pagopa.apiconfig.entity.WfespPluginConf wfespPluginConf = getWfespPluginConfigurationIfExists(idServPlugin);
        return modelMapper.map(wfespPluginConf, WfespPluginConf.class);
    }

    public WfespPluginConf createWfespPluginConfiguration(WfespPluginConf wfespPluginConf) {
        Optional<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> wp = wfespPluginConfRepository.findByIdServPlugin(wfespPluginConf.getIdServPlugin());
        if (wp.isPresent()) {
            throw new AppException(AppError.CONFIGURATION_WFESP_PLUGIN_CONFLICT, wfespPluginConf.getIdServPlugin());
        }

        it.pagopa.pagopa.apiconfig.entity.WfespPluginConf wpEntity = modelMapper.map(wfespPluginConf, it.pagopa.pagopa.apiconfig.entity.WfespPluginConf.class);
        wfespPluginConfRepository.save(wpEntity);
        return modelMapper.map(wpEntity, WfespPluginConf.class);
    }

    public WfespPluginConf updateWfespPluginConfiguration(String idServPlugin, WfespPluginConf wfespPluginConf) {
        Optional<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> wp = wfespPluginConfRepository.findByIdServPlugin(idServPlugin);
        if (!wp.isPresent()) {
            throw new AppException(AppError.CONFIGURATION_WFESP_PLUGIN_NOT_FOUND, idServPlugin);
        }

        it.pagopa.pagopa.apiconfig.entity.WfespPluginConf wpEntity = wp.get();
        wpEntity.setIdServPlugin(idServPlugin);
        wpEntity.setIdBean(wfespPluginConf.getIdBean());
        wpEntity.setProfiloPagConstString(wfespPluginConf.getProfiloPagConstString());
        wpEntity.setProfiloPagSoapRule(wfespPluginConf.getProfiloPagSoapRule());
        wpEntity.setProfiloPagRptXpath(wfespPluginConf.getProfiloPagRptXpath());

        wfespPluginConfRepository.save(wpEntity);

        return modelMapper.map(wpEntity, WfespPluginConf.class);
    }

    public void deleteWfespPluginConfiguration(String idServPlugin) {
        it.pagopa.pagopa.apiconfig.entity.WfespPluginConf wp = getWfespPluginConfigurationIfExists(idServPlugin);
        wfespPluginConfRepository.delete(wp);
    }

    public Pdds getPdds() {
        List<it.pagopa.pagopa.apiconfig.entity.Pdd> pddList = pddRepository.findAll();
        return it.pagopa.pagopa.apiconfig.model.configuration.Pdds.builder()
                .pddList(getPdds(pddList))
                .build();
    }

    public Pdd getPdd(@NotNull String idPdd) {
        it.pagopa.pagopa.apiconfig.entity.Pdd pdd = getPddIfExists(idPdd);
        return modelMapper.map(pdd, Pdd.class);
    }

    public Pdd createPdd(Pdd pdd) {
        Optional<it.pagopa.pagopa.apiconfig.entity.Pdd> optPdd = pddRepository.findByIdPdd(pdd.getIdPdd());
        if (optPdd.isPresent()) {
            throw new AppException(AppError.PDD_CONFLICT, pdd.getIdPdd());
        }

        it.pagopa.pagopa.apiconfig.entity.Pdd pddEntity = modelMapper.map(pdd, it.pagopa.pagopa.apiconfig.entity.Pdd.class);
        pddRepository.save(pddEntity);
        return modelMapper.map(pddEntity, Pdd.class);
    }

    public Pdd updatePdd(String idPdd, Pdd pdd) {
        Optional<it.pagopa.pagopa.apiconfig.entity.Pdd> optPdd = pddRepository.findByIdPdd(idPdd);
        if (optPdd.isEmpty()) {
            throw new AppException(AppError.PDD_NOT_FOUND, idPdd);
        }
        it.pagopa.pagopa.apiconfig.entity.Pdd updatedPdd = optPdd.get();
        updatedPdd.setEnabled(pdd.getEnabled());
        updatedPdd.setDescrizione(pdd.getDescription());
        updatedPdd.setIp(pdd.getIp());
        updatedPdd.setPorta(pdd.getPort());
        pddRepository.save(updatedPdd);

        return modelMapper.map(updatedPdd, Pdd.class);
    }

    public void deletePdd(String idPdd) {
        it.pagopa.pagopa.apiconfig.entity.Pdd pdd = getPddIfExists(idPdd);
        pddRepository.delete(pdd);
    }

    /**
     * Maps ConfigurationKeys objects stored in the DB in a List of ConfigurationKey
     *
     * @param configurationKeysList list of configuration key returned from the database
     * @return a list of {@link it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey}.
     */
    private List<ConfigurationKey> getConfigurationKeys(List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configurationKeysList) {
        return configurationKeysList.stream()
                .map(elem -> modelMapper.map(elem, ConfigurationKey.class))
                .collect(Collectors.toList());
    }

    /**
     * @param category configuration category
     * @param key configuration key
     * @return return the configuration key record from DB if exists
     * @throws AppException if not found
     */
    private it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys getConfigurationKeyIfExists(String category, String key) throws AppException {
        Optional<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> result = configurationKeysRepository.findByConfigCategoryAndConfigKey(category, key);
        if (result.isEmpty()) {
            throw new AppException(AppError.CONFIGURATION_KEY_NOT_FOUND, category, key);
        }
        return result.get();
    }

    /**
     * Maps WfespPluginConf objects stored in the DB in a List of WfespPluginConf
     *
     * @param wfespPluginConfList list of Wfesp Plugin configuration returned from the database
     * @return a list of {@link it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf}.
     */
    private List<WfespPluginConf> getWfespPluginConfList(List<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> wfespPluginConfList) {
        return wfespPluginConfList.stream()
                .map(elem -> modelMapper.map(elem, WfespPluginConf.class))
                .collect(Collectors.toList());
    }

    /**
     * @param idServPlugin idServPlugin
     * @return return the configuration wfesp plugin record from DB if exists
     * @throws AppException if not found
     */
    private it.pagopa.pagopa.apiconfig.entity.WfespPluginConf getWfespPluginConfigurationIfExists(String idServPlugin) throws AppException {
        Optional<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> result = wfespPluginConfRepository.findByIdServPlugin(idServPlugin);
        if (result.isEmpty()) {
            throw new AppException(AppError.CONFIGURATION_WFESP_PLUGIN_NOT_FOUND, idServPlugin);
        }
        return result.get();
    }

    /**
     * Maps Pdds objects stored in the DB in a List of Pdd
     *
     * @param pddList list of pdd returned from the database
     * @return a list of {@link it.pagopa.pagopa.apiconfig.model.configuration.Pdds}.
     */
    private List<Pdd> getPdds(List<it.pagopa.pagopa.apiconfig.entity.Pdd> pddList) {
        return pddList.stream()
                .map(elem -> modelMapper.map(elem, Pdd.class))
                .collect(Collectors.toList());
    }

    /**
     * @param idPdd pdd identifier
     * @return return the configuration key record from DB if exists
     * @throws AppException if not found
     */
    private it.pagopa.pagopa.apiconfig.entity.Pdd getPddIfExists(String idPdd) throws AppException {
        Optional<it.pagopa.pagopa.apiconfig.entity.Pdd> result = pddRepository.findByIdPdd(idPdd);
        if (result.isEmpty()) {
            throw new AppException(AppError.PDD_NOT_FOUND, idPdd);
        }
        return result.get();
    }
}
