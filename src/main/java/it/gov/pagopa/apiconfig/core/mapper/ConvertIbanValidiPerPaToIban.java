package it.gov.pagopa.apiconfig.core.mapper;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.toOffsetDateTime;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;

public class ConvertIbanValidiPerPaToIban implements Converter<IbanValidiPerPa, Iban> {

  @Override
  public Iban convert(MappingContext<IbanValidiPerPa, Iban> context) {
    @Valid IbanValidiPerPa source = context.getSource();
    return Iban.builder()
        .ibanValue(source.getIbanAccredito())
        .publicationDate(toOffsetDateTime(source.getDataPubblicazione()))
        .validityDate(toOffsetDateTime(source.getDataInizioValidita()))
        .build();
  }
}
