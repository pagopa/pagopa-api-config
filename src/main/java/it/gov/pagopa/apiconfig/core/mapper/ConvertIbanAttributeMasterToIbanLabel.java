package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIbanAttributeMasterToIbanLabel
    implements Converter<IbanAttributeMaster, IbanLabel> {

  @Override
  public IbanLabel convert(MappingContext<IbanAttributeMaster, IbanLabel> context) {
    @Valid IbanAttribute source = context.getSource().getIbanAttribute();
    return IbanLabel.builder()
        .name(source.getAttributeName())
        .description(source.getAttributeDescription())
        .build();
  }
}
