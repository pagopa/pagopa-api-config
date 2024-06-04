package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.starter.entity.Cache;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

@Slf4j
public class ConvertCacheToCacheModel implements Converter<Cache, it.gov.pagopa.apiconfig.core.model.configuration.Cache> {

  @Override
  public it.gov.pagopa.apiconfig.core.model.configuration.Cache convert(MappingContext<Cache, it.gov.pagopa.apiconfig.core.model.configuration.Cache> context) {
    @Valid Cache source = context.getSource();
    log.debug(source.toString());
    return it.gov.pagopa.apiconfig.core.model.configuration.Cache.builder()
            .id(source.getId())
            .version(source.getVersion())
            .time(Timestamp.from(source.getTime().truncatedTo(ChronoUnit.SECONDS).toInstant()).toString())
            .build();
  }
}
