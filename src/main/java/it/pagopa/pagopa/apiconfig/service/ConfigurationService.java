package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
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
    private ModelMapper modelMapper;

    public ConfigurationKeys getConfigurationKeys() {
        List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configKeyList = configurationKeysRepository.findAll();
        return it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys.builder()
                .configurationKeyList(getConfigurationKeys(configKeyList))
                .build();
    }

    public ConfigurationKey getConfigurationKey(@NotNull String key) {
        it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys configKey = getConfigurationKeyIfExists(key);
        return modelMapper.map(configKey, ConfigurationKey.class);
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
     * @param key configuration key
     * @return return the configuration key record from DB if exists
     * @throws AppException if not found
     */
    protected it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys getConfigurationKeyIfExists(String key) throws AppException {
        Optional<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> result = configurationKeysRepository.findByConfigKey(key);
        if (result.isEmpty()) {
            throw new AppException(AppError.CONFIGURATION_KEY_NOT_FOUND, key);
        }
        return result.get();
    }

}
