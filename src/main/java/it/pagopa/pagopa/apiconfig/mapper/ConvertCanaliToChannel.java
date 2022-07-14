package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.model.psp.Channel;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertCanaliToChannel implements Converter<Canali, Channel> {

    @Override
    public Channel convert(MappingContext<Canali, Channel> context) {
        @Valid Canali source = context.getSource();
        return Channel.builder()
                .channelCode(source.getIdCanale())
                .enabled(source.getEnabled())
                .brokerDescription(CommonUtil.deNull(source.getFkIntermediarioPsp().getCodiceIntermediario()))
                .build();
    }


}
