package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertPaToCreditorInstitution implements Converter<Pa, CreditorInstitution> {

    @Override
    public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
        @Valid Pa pa = context.getSource();
        return CreditorInstitution.builder()
                .creditorInstitutionCode(pa.getIdDominio())
                .enabled(pa.getEnabled())
                .businessName(pa.getRagioneSociale())
                .build();
    }
}
