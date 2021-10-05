package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.model.CounterpartTable;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toOffsetDateTime;

public class ConvertInformativeContoAccreditoMasterRepositoryToCounter implements Converter<InformativeContoAccreditoMaster, CounterpartTable> {

    @Override
    public CounterpartTable convert(MappingContext<InformativeContoAccreditoMaster, CounterpartTable> context) {
        @Valid InformativeContoAccreditoMaster source = context.getSource();
        return CounterpartTable.builder()
                .idCounterpartTable(source.getIdInformativaContoAccreditoPa())
                .creditorInstitutionCode(source.getFkPa().getIdDominio())
                .businessName(source.getFkPa().getRagioneSociale())
                .validityDate(toOffsetDateTime(source.getDataInizioValidita()))
                .publicationDate(toOffsetDateTime(source.getDataPubblicazione()))
                .build();
    }
}
