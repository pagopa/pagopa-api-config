package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.starter.entity.Pa;

public class ConvertCreditorInstitutionDetailsToPa
    implements Converter<CreditorInstitutionDetails, Pa> {

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
        .capDomicilioFiscale(source.getAddress().getZipCode())
        .denominazioneDomicilioFiscale(source.getAddress().getTaxDomicile())
        .pagamentoPressoPsp(source.getPspPayment())
        .rendicontazioneFtp(source.getReportingFtp())
        .rendicontazioneZip(source.getReportingZip())
        .flagRepoCommissioneCaricoPa(false)
        .build();
  }
}