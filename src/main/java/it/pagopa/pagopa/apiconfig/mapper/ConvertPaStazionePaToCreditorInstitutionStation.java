package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
