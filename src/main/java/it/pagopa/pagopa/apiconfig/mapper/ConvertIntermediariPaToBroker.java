package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Broker;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertIntermediariPaToBroker implements Converter<IntermediariPa, Broker> {

    @Override
    public Broker convert(MappingContext<IntermediariPa, Broker> context) {
        @Valid IntermediariPa source = context.getSource();
        return Broker.builder()
                .enabled(source.getEnabled())
                .brokerCode(source.getIdIntermediarioPa())
                .description(CommonUtil.deNull(source.getCodiceIntermediario()))
                .build();
    }
}
