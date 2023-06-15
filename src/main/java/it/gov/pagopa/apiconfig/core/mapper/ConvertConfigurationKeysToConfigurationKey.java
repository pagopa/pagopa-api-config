package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
