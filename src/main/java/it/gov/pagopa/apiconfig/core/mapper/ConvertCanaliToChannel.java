package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.Channel;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Canali;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

@Slf4j
public class ConvertCanaliToChannel implements Converter<Canali, Channel> {

    @Override
    public Channel convert(MappingContext<Canali, Channel> context) {
        @Valid Canali source = context.getSource();
        log.debug(source.toString());
        return Channel.builder()
                .channelCode(source.getIdCanale())
                .enabled(source.getEnabled())
                .primitiveVersion(source.getFkCanaliNodo() != null ? source.getFkCanaliNodo().getVersionePrimitive() : null)
                .brokerDescription(
                        CommonUtil.deNull(source.getFkIntermediarioPsp().getCodiceIntermediario()))
                .build();
    }
}
