package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertBrokerPspDetailsToIntermediariPsp implements Converter<BrokerPspDetails, IntermediariPsp> {

    @Override
    public IntermediariPsp convert(MappingContext<BrokerPspDetails, IntermediariPsp> context) {
        @Valid BrokerPspDetails source = context.getSource();
        return IntermediariPsp.builder()
                .enabled(source.getEnabled())
                .idIntermediarioPsp(source.getBrokerPspCode())
                .codiceIntermediario(source.getDescription())
                .faultBeanEsteso(source.getExtendedFaultBean())
                .intermediarioAvv(false)
                .intermediarioNodo(true)
                .build();
    }
}
