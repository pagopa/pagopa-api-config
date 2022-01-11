package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertIntermediariPaToBrokerDetails implements Converter<IntermediariPa, BrokerDetails> {

    @Override
    public BrokerDetails convert(MappingContext<IntermediariPa, BrokerDetails> context) {
        @Valid IntermediariPa source = context.getSource();
        return BrokerDetails.builder()
                .enabled(source.getEnabled())
                .brokerCode(source.getIdIntermediarioPa())
                .description(CommonUtil.deNull(source.getCodiceIntermediario()))
                .extendedFaultBean(source.getFaultBeanEsteso())
                .build();
    }
}
