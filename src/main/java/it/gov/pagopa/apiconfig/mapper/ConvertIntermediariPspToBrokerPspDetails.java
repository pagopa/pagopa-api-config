package it.gov.pagopa.apiconfig.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertIntermediariPspToBrokerPspDetails
    implements Converter<IntermediariPsp, BrokerPspDetails> {
  @Override
  public BrokerPspDetails convert(MappingContext<IntermediariPsp, BrokerPspDetails> context) {
    IntermediariPsp source = context.getSource();
    return BrokerPspDetails.builder()
        .brokerPspCode(source.getIdIntermediarioPsp())
        .enabled(source.getEnabled())
        .description(CommonUtil.deNull(source.getCodiceIntermediario()))
        .extendedFaultBean(source.getFaultBeanEsteso())
        .build();
  }
}
