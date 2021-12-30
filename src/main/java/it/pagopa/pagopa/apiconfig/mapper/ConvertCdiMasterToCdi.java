package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertCdiMasterToCdi implements Converter<CdiMaster, Cdi> {

    @Override
    public Cdi convert(MappingContext<CdiMaster, Cdi> context) {
        @Valid CdiMaster source = context.getSource();
        return Cdi.builder()
                .idCdi(source.getIdInformativaPsp())
                .pspCode(source.getFkPsp().getIdPsp())
                .businessName(source.getFkPsp().getRagioneSociale())
                .publicationDate(CommonUtil.toOffsetDateTime(source.getDataPubblicazione()))
                .validityDate(CommonUtil.toOffsetDateTime(source.getDataInizioValidita()))
                .build();
    }
}
