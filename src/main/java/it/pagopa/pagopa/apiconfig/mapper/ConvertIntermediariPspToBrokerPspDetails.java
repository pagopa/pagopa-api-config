package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIntermediariPspToBrokerPspDetails implements Converter<IntermediariPsp, BrokerPspDetails> {
    @Override
    public BrokerPspDetails convert(MappingContext<IntermediariPsp, BrokerPspDetails> context) {
        IntermediariPsp source = context.getSource();
        return BrokerPspDetails.builder()
                .brokerPspCode(source.getIdIntermediarioPsp())
                .enabled(source.getEnabled())
                .description(source.getCodiceIntermediario())
                .extendedFaultBean(source.getFaultBeanEsteso())
                .build();
    }
}
