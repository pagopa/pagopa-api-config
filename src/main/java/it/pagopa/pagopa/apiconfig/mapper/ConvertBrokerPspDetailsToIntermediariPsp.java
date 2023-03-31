package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;

public class ConvertBrokerPspDetailsToIntermediariPsp
    implements Converter<BrokerPspDetails, IntermediariPsp> {

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
