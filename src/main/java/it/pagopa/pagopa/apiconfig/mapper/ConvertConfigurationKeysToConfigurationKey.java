package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys;
import it.pagopa.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertConfigurationKeysToConfigurationKey implements Converter<ConfigurationKeys, ConfigurationKey> {
    @Override
    public ConfigurationKey convert(MappingContext<ConfigurationKeys, ConfigurationKey> mappingContext) {
        @Valid ConfigurationKeys configurationKeys = mappingContext.getSource();
        return ConfigurationKey.builder()
                .configCategory(configurationKeys.getConfigCategory())
                .configKey(configurationKeys.getConfigKey())
                .configValue(configurationKeys.getConfigValue())
                .configDescription(CommonUtil.deNull(configurationKeys.getConfigDescription()))
                .build();
    }
}
