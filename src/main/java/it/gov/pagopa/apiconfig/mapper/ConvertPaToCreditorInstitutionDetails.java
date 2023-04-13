package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionAddress;
import it.gov.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertPaToCreditorInstitutionDetails
    implements Converter<Pa, CreditorInstitutionDetails> {

  @Override
  public CreditorInstitutionDetails convert(
      MappingContext<Pa, CreditorInstitutionDetails> context) {
    @Valid Pa pa = context.getSource();
    return CreditorInstitutionDetails.builder()
        .creditorInstitutionCode(pa.getIdDominio())
        .enabled(pa.getEnabled())
        .businessName(CommonUtil.deNull(pa.getRagioneSociale()))
        .address(
            CreditorInstitutionAddress.builder()
                .city(pa.getComuneDomicilioFiscale())
                .location(pa.getIndirizzoDomicilioFiscale())
                .countryCode(pa.getSiglaProvinciaDomicilioFiscale())
                .zipCode(pa.getCapDomicilioFiscale())
                .taxDomicile(pa.getDenominazioneDomicilioFiscale())
                .build())
        .pspPayment(pa.getPagamentoPressoPsp())
        .reportingFtp(pa.getRendicontazioneFtp())
        .reportingZip(pa.getRendicontazioneZip())
        .build();
  }
}
