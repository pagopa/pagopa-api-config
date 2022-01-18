package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertCreditorInstitutionStationPostToPaStazionePa implements Converter<CreditorInstitutionStationEdit, PaStazionePa> {

    @Override
    public PaStazionePa convert(MappingContext<CreditorInstitutionStationEdit, PaStazionePa> context) {
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
