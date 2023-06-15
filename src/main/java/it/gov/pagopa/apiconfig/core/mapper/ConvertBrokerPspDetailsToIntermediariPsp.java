package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.BrokerPspDetails;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
