package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaToCreditorInstitution implements Converter<Pa, CreditorInstitution> {

  @Override
  public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
    @Valid Pa pa = context.getSource();
    return CreditorInstitution.builder()
        .creditorInstitutionCode(pa.getIdDominio())
        .enabled(pa.getEnabled())
        .businessName(CommonUtil.deNull((pa.getRagioneSociale())))
        .description(pa.getDescription())
        .build();
  }
}
