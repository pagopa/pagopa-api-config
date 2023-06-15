package it.gov.pagopa.apiconfig.core.mapper;

import static it.gov.pagopa.apiconfig.core.util.CommonUtil.toOffsetDateTime;

import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Ica;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.InformativeContoAccreditoMaster;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertInformativeContoAccreditoMasterRepositoryToIca
    implements Converter<InformativeContoAccreditoMaster, Ica> {

  @Override
  public Ica convert(MappingContext<InformativeContoAccreditoMaster, Ica> context) {
    @Valid InformativeContoAccreditoMaster source = context.getSource();
    return Ica.builder()
        .idIca(source.getIdInformativaContoAccreditoPa())
        .creditorInstitutionCode(source.getFkPa().getIdDominio())
        .businessName(CommonUtil.deNull(source.getFkPa().getRagioneSociale()))
        .validityDate(toOffsetDateTime(source.getDataInizioValidita()))
        .publicationDate(toOffsetDateTime(source.getDataPubblicazione()))
        .build();
  }
}
