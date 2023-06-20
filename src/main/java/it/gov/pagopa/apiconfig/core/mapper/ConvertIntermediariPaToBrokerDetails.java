package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.BrokerDetails;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
