package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertConfigurationKeysToConfigurationKey
    implements Converter<ConfigurationKeys, ConfigurationKey> {
  @Override
  public ConfigurationKey convert(
      MappingContext<ConfigurationKeys, ConfigurationKey> mappingContext) {
    @Valid ConfigurationKeys configurationKeys = mappingContext.getSource();
    return ConfigurationKey.builder()
        .configCategory(configurationKeys.getConfigCategory())
        .configKey(configurationKeys.getConfigKey())
        .configValue(configurationKeys.getConfigValue())
        .configDescription(CommonUtil.deNull(configurationKeys.getConfigDescription()))
        .build();
  }
}
