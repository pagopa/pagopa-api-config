package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;

public class ConvertCodifichePaToEncoding implements Converter<CodifichePa, Encoding> {

  @Override
  public Encoding convert(MappingContext<CodifichePa, Encoding> context) {
    @Valid CodifichePa source = context.getSource();
    return Encoding.builder()
        .codeType(getCodeType(source))
        .encodingCode(source.getCodicePa())
        .build();
  }

  /**
   * Null-safe conversion
   *
   * @param source {@link CodifichePa}
   * @return {@link Encoding.CodeTypeEnum}
   */
  private Encoding.CodeTypeEnum getCodeType(CodifichePa source) {
    if (source != null && source.getFkCodifica() != null) {
      return Encoding.CodeTypeEnum.fromValue(source.getFkCodifica().getIdCodifica());
    } else {
      return null;
    }
  }
}
