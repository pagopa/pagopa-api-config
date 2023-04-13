package it.gov.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;

public class ConvertCreditorInstitutionStationPostToPaStazionePa
    implements Converter<CreditorInstitutionStationEdit, PaStazionePa> {

  @Override
  public PaStazionePa convert(
      MappingContext<CreditorInstitutionStationEdit, PaStazionePa> context) {
    @Valid CreditorInstitutionStationEdit source = context.getSource();
    return PaStazionePa.builder()
        .segregazione(source.getSegregationCode())
        .quartoModello(source.getMod4())
        .broadcast(source.getBroadcast())
        .progressivo(source.getApplicationCode())
        .auxDigit(source.getAuxDigit())
        .pa(source.getFkPa())
        .fkStazione(source.getFkStazioni())
        .stazioneAvv(false) // default
        .stazioneNodo(true) // default
        .build();
  }
}
