package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.ElencoServizi;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertElencoServiziToService implements Converter<ElencoServizi, Service> {

    @Override
    public Service convert(MappingContext<ElencoServizi, Service> context) {
        @Valid ElencoServizi source = context.getSource();
        return Service.builder()
                .pspCode(source.getPspId())
                .flowId(source.getFlussoId())
                .pspBusinessName(source.getPspRagSoc())
                .pspFlagStamp(source.getPspFlagBollo())
                .brokerPspCode(source.getIntmId())
                .channelCode(source.getCanaleId())
                .serviceName(source.getNomeServizio())
                .paymentMethodChannel(source.getCanaleModPag())
                .paymentTypeCode(getPaymentTypeCode(source))
                .languageCode(getLanguageCode(source))
                .serviceDescription(source.getNomeServizio())
                .serviceAvailability(source.getInfDispServ())
                .channelUrl(source.getInfUrlCanale())
                .minimumAmount(source.getImportoMinimo())
                .maximumAmount(source.getImportoMassimo())
                .fixedCost(source.getCostoFisso())
                .timestampInsertion(CommonUtil.toOffsetDateTime(source.getTimestampIns()))
                .validityDate(CommonUtil.toOffsetDateTime(source.getDataValidita()))
                .logoPsp(source.getLogoPsp())
                .tags(source.getTags())
                .logoService(source.getLogoServizio())
                .channelApp(source.getCanaleApp())
                .onUs(source.getOnUs())
                .cartCard(source.getCarrelloCarte())
                .abiCode(source.getCodiceAbi())
                .mybankCode(source.getCodiceMybank())
                .conventionCode(source.getCodiceConvenzione())
                .flagIo(source.getFlagIo())
                .build();
    }

    private Service.LanguageCode getLanguageCode(ElencoServizi source) {
        return source.getCodiceLingua() != null ? Service.LanguageCode.valueOf(source.getCodiceLingua()) : null;
    }

    private Service.PaymentTypeCode getPaymentTypeCode(ElencoServizi source) {
        return source.getTipoVersCod() != null ? Service.PaymentTypeCode.valueOf(source.getTipoVersCod()) : null;
    }
}
