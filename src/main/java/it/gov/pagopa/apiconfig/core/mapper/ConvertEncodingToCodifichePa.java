package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertEncodingToCodifichePa implements Converter<Encoding, CodifichePa> {

  @Override
  public CodifichePa convert(MappingContext<Encoding, CodifichePa> context) {
    @Valid Encoding source = context.getSource();
    return CodifichePa.builder()
        .codicePa(source.getEncodingCode())
        .fkCodifica(
            Codifiche.builder()
                .objId(source.getCodificheObjId())
                .idCodifica(source.getCodeType().getValue())
                .build())
        .fkPa(Pa.builder().objId(source.getPaObjId()).build())
        .build();
  }
}
