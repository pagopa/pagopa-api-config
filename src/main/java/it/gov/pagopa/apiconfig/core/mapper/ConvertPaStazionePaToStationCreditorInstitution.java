package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

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
                .description(pa.getDescription())
                .enabled(pa.getEnabled())
                .auxDigit(source.getAuxDigit())
                .applicationCode(source.getProgressivo())
                .broadcast(source.getBroadcast())
                .mod4(source.getQuartoModello())
                .segregationCode(source.getSegregazione())
                .aca(source.getAca())
                .standIn(source.getStandin())
                .spontaneousPayment(source.getPagamentoSpontaneo())
                .build();
    }
}
