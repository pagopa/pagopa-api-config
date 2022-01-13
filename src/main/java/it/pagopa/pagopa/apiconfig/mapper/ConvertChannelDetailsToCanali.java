package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.CanaliNodo;
import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;


public class ConvertChannelDetailsToCanali implements Converter<ChannelDetails, Canali> {

    @Override
    public Canali convert(MappingContext<ChannelDetails, Canali> context) {
        @Valid ChannelDetails source = context.getSource();
        return Canali.builder()
                .idCanale(source.getChannelCode())
                .enabled(source.getEnabled())
                .ip(source.getIp())
                .newPassword(source.getNewPassword())
                .password(source.getPassword())
                .porta(source.getPort())
                .protocollo(source.getProtocol())
                .servizio(source.getService())
                .descrizione(source.getDescription())
                .fkIntermediarioPsp(source.getFkIntermediarioPsp())
                .proxyEnabled(source.getProxyEnabled())
                .proxyHost(source.getProxyHost())
                .proxyPassword(source.getProxyPassword())
                .proxyPort(source.getProxyPort())
                .proxyUsername(source.getProxyUsername())
                .canaleNodo(true)
                .canaleAvv(false)
                .fkCanaliNodo(buildCanaliNodo(source))
                .timeout(120L)
                .numThread(source.getThreadNumber())
                .useNewFaultCode(source.getNewFaultCode())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .servizioNmp(source.getNpmService())
                .build();
    }

    private CanaliNodo buildCanaliNodo(ChannelDetails source) {
        return CanaliNodo.builder()
                .redirectIp(source.getRedirectIp())
                .redirectPath(source.getRedirectPath())
                .redirectPorta(source.getRedirectPort())
                .redirectQueryString(source.getRedirectQueryString())
                .redirectProtocollo(source.getRedirectProtocol())
                .modelloPagamento(source.getPaymentModel().getDatabaseValue())
                .idServPlugin(buildServPlugin(source.getServPlugin()))
                .rtPush(source.getRtPush())
                .onUs(source.getOnUs())
                .carrelloCarte(source.getCardChart())
                .recovery(source.getRecovery())
                .marcaBolloDigitale(source.getDigitalStampBrand())
                .flagIo(source.getFlagIo())
                .build();
    }

    private WfespPluginConf buildServPlugin(String servPlugin) {
        return WfespPluginConf.builder()
                .idServPlugin(servPlugin)
                .build();
    }


}
