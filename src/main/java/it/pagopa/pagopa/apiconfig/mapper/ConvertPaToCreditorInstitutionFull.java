package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionFull;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.flagToBoolean;

public class ConvertPaToCreditorInstitutionFull implements Converter<Pa, CreditorInstitutionFull> {

    @Override
    public CreditorInstitutionFull convert(MappingContext<Pa, CreditorInstitutionFull> context) {
        @Valid Pa pa = context.getSource();
        return CreditorInstitutionFull.builder()
                .organizationFiscalCode(pa.getIdDominio())
                .enabled(flagToBoolean(pa.getEnabled()))
                .businessName(pa.getRagioneSociale())
                .address(CreditorInstitutionAddress.builder()
                        .city(pa.getComuneDomicilioFiscale())
                        .location(pa.getIndirizzoDomicilioFiscale())
                        .countryCode(pa.getSiglaProvinciaDomicilioFiscale())
                        .zipCode(CommonUtil.numberToZipCode(pa.getCapDomicilioFiscale()))
                        .build())
                .pspPayment(flagToBoolean(pa.getPagamentoPressoPsp()))
                .reportingFtp(flagToBoolean(pa.getRendicontazioneFtp()))
                .reportingZip(flagToBoolean(pa.getRendicontazioneZip()))
                .build();
    }
}
