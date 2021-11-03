package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertEncodingToCodifichePa implements Converter<Encoding, CodifichePa> {

    @Override
    public CodifichePa convert(MappingContext<Encoding, CodifichePa> context) {
        @Valid Encoding source = context.getSource();
        return CodifichePa.builder()
                .codicePa(source.getEncodingCode())
                .fkCodifica(Codifiche.builder()
                        .objId(source.getCodificheObjId())
                        .idCodifica(source.getCodeType().getValue())
                        .build())
                .fkPa(Pa.builder()
                        .objId(source.getPaObjId())
                        .build())
                .build();
    }

}
