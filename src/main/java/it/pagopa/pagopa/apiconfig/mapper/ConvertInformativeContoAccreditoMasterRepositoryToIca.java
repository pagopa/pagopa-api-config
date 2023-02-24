package it.pagopa.pagopa.apiconfig.mapper;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.toOffsetDateTime;

import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
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
