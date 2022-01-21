package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
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
        if (!configKey.isPresent()) {
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

    public WfespPluginConfs getWfespPluginConfigurationList() {
        List<it.pagopa.pagopa.apiconfig.entity.WfespPluginConf> list = wfespPluginConfRepository.findAll();
        return it.pagopa.pagopa.apiconfig.model.configuration.WfespPluginConfs.builder()
                .wfespPluginConfList(getWfespPluginConfList(list))
                .build();
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

}
