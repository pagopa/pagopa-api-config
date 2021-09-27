package it.pagopa.pagopa.apiconfig.mapper;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.StationCI;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import javax.validation.Valid;

public class ConvertPaStazionePaToStationCI implements Converter<PaStazionePa, StationCI> {

    @Override
    public StationCI convert(MappingContext<PaStazionePa, StationCI> context) {
        @Valid PaStazionePa source = context.getSource();
        Stazioni stazione = source.getFkStazione();
        return StationCI.builder()
                .enabled(stazione.getEnabled())
                .idStation(stazione.getIdStazione())
                .version(stazione.getVersione())
                .ip(stazione.getIp())
                .newPassword(stazione.getNewPassword())
                .password(stazione.getPassword())
                .port(stazione.getPorta())
                .redirectIp(stazione.getRedirectIp())
                .redirectPath(stazione.getRedirectPath())
                .redirectPort(stazione.getRedirectPorta())
                .redirectQueryString(stazione.getRedirectQueryString())
                .service(stazione.getServizio())
                .rtEnabled(stazione.getRtEnabled())
                .pofService(stazione.getServizioPof())
                .intermediaryId(stazione.getFkIntermediarioPa() != null ? stazione.getFkIntermediarioPa().getObjId() : null)
                .redirectProtocol(stazione.getRedirectProtocollo())
                .protocol4Mod(stazione.getIp4Mod())
                .ip4Mod(stazione.getIp4Mod())
                .port4Mod(stazione.getPorta4Mod())
                .service4Mod(stazione.getServizio4Mod())
                .proxyEnabled(stazione.getProxyEnabled())
                .proxyHost(stazione.getProxyHost())
                .proxyPort(stazione.getProxyPort())
                .proxyUsername(stazione.getProxyUsername())
                .proxyPassword(stazione.getProxyPassword())
                .protocolAvv(stazione.getProtocolloAvv())
                .threadNumber(stazione.getNumThread())
                .timeoutA(stazione.getTimeoutA())
                .timeoutB(stazione.getTimeoutB())
                .timeoutC(stazione.getTimeoutC())
                .flagOnline(stazione.getFlagOnline())
                .npmService(stazione.getServizioNmp())
                .auxDigit(source.getAuxDigit())
                .noticeNumber(source.getProgressivo())
                .broadcast(source.getBroadcast())
                .fourthModel(source.getQuartoModello())
                .segregationNumber(source.getSegregazione())
                .build();
    }
}
