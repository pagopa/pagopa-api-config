package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionAddress;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertPaToCreditorInstitutionDetails implements Converter<Pa, CreditorInstitutionDetails> {

    @Override
    public CreditorInstitutionDetails convert(MappingContext<Pa, CreditorInstitutionDetails> context) {
        @Valid Pa pa = context.getSource();
        return CreditorInstitutionDetails.builder()
                .creditorInstitutionCode(pa.getIdDominio())
                .enabled(pa.getEnabled())
                .businessName(CommonUtil.deNull(pa.getRagioneSociale()))
                .address(CreditorInstitutionAddress.builder()
                        .city(pa.getComuneDomicilioFiscale())
                        .location(pa.getIndirizzoDomicilioFiscale())
                        .countryCode(pa.getSiglaProvinciaDomicilioFiscale())
                        .zipCode(CommonUtil.numberToZipCode(pa.getCapDomicilioFiscale()))
                        .taxDomicile(pa.getDenominazioneDomicilioFiscale())
                        .build())
                .pspPayment(pa.getPagamentoPressoPsp())
                .reportingFtp(pa.getRendicontazioneFtp())
                .reportingZip(pa.getRendicontazioneZip())
                .build();
    }
}
