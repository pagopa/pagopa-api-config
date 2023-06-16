package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
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
