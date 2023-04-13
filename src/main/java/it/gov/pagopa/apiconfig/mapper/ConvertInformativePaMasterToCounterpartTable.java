package it.gov.pagopa.apiconfig.mapper;

import static it.gov.pagopa.apiconfig.util.CommonUtil.toOffsetDateTime;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.model.creditorinstitution.CounterpartTable;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.util.CommonUtil;

public class ConvertInformativePaMasterToCounterpartTable
    implements Converter<InformativePaMaster, CounterpartTable> {

  @Override
  public CounterpartTable convert(MappingContext<InformativePaMaster, CounterpartTable> context) {
    @Valid InformativePaMaster source = context.getSource();
    return CounterpartTable.builder()
        .idCounterpartTable(source.getIdInformativaPa())
        .creditorInstitutionCode(source.getFkPa().getIdDominio())
        .businessName(CommonUtil.deNull(source.getFkPa().getRagioneSociale()))
        .validityDate(toOffsetDateTime(source.getDataInizioValidita()))
        .publicationDate(toOffsetDateTime(source.getDataPubblicazione()))
        .build();
  }
}
