package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.repository.ConfigurationKeysRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Francesco Cesareo
 * Email: cesareo.francesco@gmail.com
 */
@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationKeysRepository configurationKeysRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Maps ConfigurationKey objects stored in the DB in a List of ConfigurationKey
     *
     * @param configurationKeysList list of configuration key returned from the database
     * @return a list of {@link it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey}.
     */
    private List<ConfigurationKey> getConfigurationKeys(List<it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys> configurationKeysList) {
        return configurationKeysList.stream()
                .map(elem -> modelMapper.map(elem, ConfigurationKey.class))
                .collect(Collectors.toList());
    }

    public ConfigurationKeys getConfigurationKeys() {
        return it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKeys.builder()
                .configurationKeyList(getConfigurationKeys(configurationKeysRepository.findAll()))
                .build();
    }
}
