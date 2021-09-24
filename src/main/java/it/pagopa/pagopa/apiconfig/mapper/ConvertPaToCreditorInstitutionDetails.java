package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.flagToBoolean;

public class ConvertPaToCreditorInstitutionDetails implements Converter<Pa, CreditorInstitutionDetails> {

    @Override
    public CreditorInstitutionDetails convert(MappingContext<Pa, CreditorInstitutionDetails> context) {
        @Valid Pa pa = context.getSource();
        return CreditorInstitutionDetails.builder()
                .creditorInstitutionCode(pa.getIdDominio())
                .enabled(flagToBoolean(pa.getEnabled()))
                .businessName(pa.getRagioneSociale())
                .address(CreditorInstitutionAddress.builder()
                        .city(pa.getComuneDomicilioFiscale())
                        .location(pa.getIndirizzoDomicilioFiscale())
                        .countryCode(pa.getSiglaProvinciaDomicilioFiscale())
                        .zipCode(CommonUtil.numberToZipCode(pa.getCapDomicilioFiscale()))
                        .taxDomicile(pa.getDenominazioneDomicilioFiscale())
                        .build())
                .pspPayment(flagToBoolean(pa.getPagamentoPressoPsp()))
                .reportingFtp(flagToBoolean(pa.getRendicontazioneFtp()))
                .reportingZip(flagToBoolean(pa.getRendicontazioneZip()))
                .description(pa.getDescrizione())
                .fkQuadrature(pa.getFkIntQuadrature())
                .build();
    }
}
