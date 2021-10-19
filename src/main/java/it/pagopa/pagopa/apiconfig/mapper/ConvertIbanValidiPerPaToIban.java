package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.model.Iban;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toOffsetDateTime;

@Component
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
