package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.BrokerDetails;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;

public class ConvertBrokerDetailsToIntermediariPa
    implements Converter<BrokerDetails, IntermediariPa> {

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
