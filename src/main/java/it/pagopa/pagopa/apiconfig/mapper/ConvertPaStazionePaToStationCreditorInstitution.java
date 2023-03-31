package it.pagopa.pagopa.apiconfig.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationCreditorInstitution;

public class ConvertPaStazionePaToStationCreditorInstitution
    implements Converter<PaStazionePa, StationCreditorInstitution> {

  @Override
  public StationCreditorInstitution convert(
      MappingContext<PaStazionePa, StationCreditorInstitution> context) {
    @Valid PaStazionePa source = context.getSource();
    Pa pa = source.getPa();
    return StationCreditorInstitution.builder()
        .creditorInstitutionCode(pa.getIdDominio())
        .businessName(pa.getRagioneSociale())
        .enabled(pa.getEnabled())
        .auxDigit(source.getAuxDigit())
        .applicationCode(source.getProgressivo())
        .broadcast(source.getBroadcast())
        .mod4(source.getQuartoModello())
        .segregationCode(source.getSegregazione())
        .build();
  }
}
