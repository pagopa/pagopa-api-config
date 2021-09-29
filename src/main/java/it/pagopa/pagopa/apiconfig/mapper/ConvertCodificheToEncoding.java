package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertCodificheToEncoding implements Converter<Codifiche, Encoding> {

    @Override
    public Encoding convert(MappingContext<Codifiche, Encoding> context) {
        @Valid Codifiche source = context.getSource();
        return Encoding.builder()
                .codeType(Encoding.CodeTypeEnum.fromValue(source.getIdCodifica()))
                .build();
    }
}
