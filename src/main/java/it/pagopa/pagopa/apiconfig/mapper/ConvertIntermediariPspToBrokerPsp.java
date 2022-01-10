package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPsp;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIntermediariPspToBrokerPsp implements Converter<IntermediariPsp, BrokerPsp> {
    @Override
    public BrokerPsp convert(MappingContext<IntermediariPsp, BrokerPsp> context) {
        IntermediariPsp source = context.getSource();
        return BrokerPsp.builder()
                .brokerPspCode(source.getIdIntermediarioPsp())
                .enabled(source.getEnabled())
                .description(CommonUtil.deNull(source.getCodiceIntermediario()))
                .build();
    }
}
