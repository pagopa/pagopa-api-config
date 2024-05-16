package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionView;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertPaStazionePaToCreditorInstitutionView
        implements Converter<PaStazionePa, CreditorInstitutionView> {

    @Override
    public CreditorInstitutionView convert(
            MappingContext<PaStazionePa, CreditorInstitutionView> context) {
        @Valid PaStazionePa source = context.getSource();
        return CreditorInstitutionView.builder()
                .idDominio(source.getPa().getIdDominio())
                .idIntermediarioPa(source.getFkStazione().getIntermediarioPa().getIdIntermediarioPa())
                .idStazione(source.getFkStazione().getIdStazione())
                .auxDigit(source.getAuxDigit())
                .progressivo(source.getProgressivo())
                .segregazione(source.getSegregazione())
                .quartoModello(source.getQuartoModello())
                .stationEnabled(source.getFkStazione().getEnabled())
                .build();
    }
}
