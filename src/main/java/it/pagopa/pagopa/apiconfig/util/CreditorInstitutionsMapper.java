package it.pagopa.pagopa.apiconfig.util;

import it.pagopa.pagopa.apiconfig.entities.Pa;
import it.pagopa.pagopa.apiconfig.models.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.models.CreditorInstitutionAddress;
import lombok.experimental.UtilityClass;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.flagToBoolean;

@UtilityClass
public class CreditorInstitutionsMapper {

    public CreditorInstitution mapPaToCreditorInstitution(Pa pa) {
        return CreditorInstitution.builder()
                .idDominio(pa.getIdDominio())
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
