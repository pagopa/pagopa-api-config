package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.model.CounterpartTable;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toOffsetDateTime;

@Component
public class ConvertInformativePaMasterToCounterpartTable implements Converter<InformativePaMaster, CounterpartTable> {

    @Override
    public CounterpartTable convert(MappingContext<InformativePaMaster, CounterpartTable> context) {
        @Valid InformativePaMaster source = context.getSource();
        return CounterpartTable.builder()
                .idCounterpartTable(source.getIdInformativaPa())
                .creditorInstitutionCode(source.getFkPa().getIdDominio())
                .businessName(source.getFkPa().getRagioneSociale())
                .validityDate(toOffsetDateTime(source.getDataInizioValidita()))
                .publicationDate(toOffsetDateTime(source.getDataPubblicazione()))
                .build();
    }
}
