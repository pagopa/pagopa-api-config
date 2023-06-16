package it.gov.pagopa.apiconfig.core.mapper;

import it.gov.pagopa.apiconfig.core.model.psp.Cdi;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import javax.validation.Valid;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCdiMasterToCdi implements Converter<CdiMaster, Cdi> {

  @Override
  public Cdi convert(MappingContext<CdiMaster, Cdi> context) {
    @Valid CdiMaster source = context.getSource();
    return Cdi.builder()
        .idCdi(source.getIdInformativaPsp())
        .pspCode(source.getFkPsp().getIdPsp())
        .businessName(CommonUtil.deNull(source.getFkPsp().getRagioneSociale()))
        .publicationDate(CommonUtil.toOffsetDateTime(source.getDataPubblicazione()))
        .validityDate(CommonUtil.toOffsetDateTime(source.getDataInizioValidita()))
        .build();
  }
}
