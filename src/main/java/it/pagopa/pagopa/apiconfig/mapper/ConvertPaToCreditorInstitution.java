package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;

public class ConvertPaToCreditorInstitution implements Converter<Pa, CreditorInstitution> {

  @Override
  public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
    @Valid Pa pa = context.getSource();
    return CreditorInstitution.builder()
        .creditorInstitutionCode(pa.getIdDominio())
        .enabled(pa.getEnabled())
        .businessName(CommonUtil.deNull((pa.getRagioneSociale())))
        .build();
  }
}
