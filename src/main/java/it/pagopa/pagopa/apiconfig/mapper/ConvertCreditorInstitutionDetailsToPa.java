package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertCreditorInstitutionDetailsToPa implements Converter<CreditorInstitutionDetails, Pa> {

    @Override
    public Pa convert(MappingContext<CreditorInstitutionDetails, Pa> context) {
        @Valid CreditorInstitutionDetails source = context.getSource();
        return Pa.builder()
                .idDominio(source.getCreditorInstitutionCode())
                .enabled(source.getEnabled())
                .ragioneSociale(source.getBusinessName())
                .comuneDomicilioFiscale(source.getAddress().getCity())
                .indirizzoDomicilioFiscale(source.getAddress().getLocation())
                .siglaProvinciaDomicilioFiscale(source.getAddress().getCountryCode())
                .capDomicilioFiscale(getCapDomicilioFiscale(source))
                .denominazioneDomicilioFiscale(source.getAddress().getTaxDomicile())
                .pagamentoPressoPsp(source.getPspPayment())
                .rendicontazioneFtp(source.getReportingFtp())
                .rendicontazioneZip(source.getReportingZip())
                .flagRepoCommissioneCaricoPa(false)
                .build();
    }

    private Long getCapDomicilioFiscale(CreditorInstitutionDetails source) {
        return source.getAddress().getZipCode() != null ? Long.valueOf(source.getAddress().getZipCode()) : null;
    }
}
