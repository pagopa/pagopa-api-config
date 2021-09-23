package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionLight;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.flagToBoolean;

public class ConvertPaToCreditorInstitutionLight implements Converter<Pa, CreditorInstitutionLight> {

    @Override
    public CreditorInstitutionLight convert(MappingContext<Pa, CreditorInstitutionLight> context) {
        @Valid Pa pa = context.getSource();
        return CreditorInstitutionLight.builder()
                .organizationFiscalCode(pa.getIdDominio())
                .enabled(flagToBoolean(pa.getEnabled()))
                .businessName(pa.getRagioneSociale())
                .build();
    }
}
