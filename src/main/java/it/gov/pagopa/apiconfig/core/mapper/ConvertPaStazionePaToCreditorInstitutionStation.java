package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionStation;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;

public class ConvertPaStazionePaToCreditorInstitutionStation
    implements Converter<PaStazionePa, CreditorInstitutionStation> {

  @Override
  public CreditorInstitutionStation convert(
      MappingContext<PaStazionePa, CreditorInstitutionStation> context) {
    @Valid PaStazionePa source = context.getSource();
    Stazioni stazione = source.getFkStazione();
    return CreditorInstitutionStation.builder()
        .enabled(stazione.getEnabled())
        .stationCode(stazione.getIdStazione())
        .version(stazione.getVersione())
        .auxDigit(source.getAuxDigit())
        .applicationCode(source.getProgressivo())
        .broadcast(source.getBroadcast())
        .mod4(source.getQuartoModello())
        .segregationCode(source.getSegregazione())
        .build();
  }
}