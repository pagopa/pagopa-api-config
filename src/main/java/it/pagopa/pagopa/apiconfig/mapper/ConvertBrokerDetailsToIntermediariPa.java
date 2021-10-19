package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.model.BrokerDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
public class ConvertBrokerDetailsToIntermediariPa implements Converter<BrokerDetails, IntermediariPa> {

    @Override
    public IntermediariPa convert(MappingContext<BrokerDetails, IntermediariPa> context) {
        @Valid BrokerDetails source = context.getSource();
        return IntermediariPa.builder()
                .enabled(source.getEnabled())
                .idIntermediarioPa(source.getBrokerCode())
                .codiceIntermediario(source.getDescription())
                .faultBeanEsteso(source.getExtendedFaultBean())
                .build();
    }
}
