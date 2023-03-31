package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;

public class ConvertIntermediariPaToBrokerDetails
    implements Converter<IntermediariPa, BrokerDetails> {

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
