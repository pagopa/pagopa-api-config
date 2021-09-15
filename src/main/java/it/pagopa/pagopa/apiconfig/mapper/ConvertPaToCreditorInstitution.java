package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.flagToBoolean;

public class ConvertPaToCreditorInstitution implements Converter<Pa, CreditorInstitution> {

    @Override
    public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
        Pa pa = context.getSource();
        return CreditorInstitution.builder()
                .idDomain(pa.getIdDominio())
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
