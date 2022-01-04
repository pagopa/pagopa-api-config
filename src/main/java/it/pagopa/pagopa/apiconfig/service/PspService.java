package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannel;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelList;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PspService {
    @Autowired
    PspRepository pspRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    public PaymentServiceProviders getPaymentServiceProviders(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Psp> page = pspRepository.findAll(pageable);
        return PaymentServiceProviders.builder()
                .paymentServiceProviderList(getPaymentServiceProviderList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public PaymentServiceProviderDetails getPaymentServiceProvider(@NotNull String pspCode) {
        Psp psp = getPspIfExists(pspCode);
        return modelMapper.map(psp, PaymentServiceProviderDetails.class);
    }


    public PspChannelList getPaymentServiceProvidersChannels(@NotBlank String pspCode) {
        Psp psp = getPspIfExists(pspCode);
        List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList = pspCanaleTipoVersamentoRepository.findByFkPsp(psp.getObjId());
        // data structure useful for mapping
        Map<String, Set<String>> channelPaymentType = pspCanaleTipoVersamentotoMap(pspCanaleTipoVersamentoList);
        return PspChannelList.builder()
                .channelsList(getChannelsList(pspCanaleTipoVersamentoList, channelPaymentType))
                .build();
    }

    /**
     * @param pspCanaleTipoVersamentoList list of record of PspCanaleTipoVersamento from DB
     * @return the data structure with the PaymentTypeCode associated at one Channel
     */
    private Map<String, Set<String>> pspCanaleTipoVersamentotoMap(List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList) {
        Map<String, Set<String>> map = new HashMap<>();
        pspCanaleTipoVersamentoList
                .forEach(elem -> {
                    String idCanale = elem.getCanaleTipoVersamento().getCanale().getIdCanale();
                    String tipoVersamento = elem.getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento();
                    if (!map.containsKey(idCanale)) {
                        map.put(idCanale, new HashSet<>(List.of(tipoVersamento)));
                    } else {
                        map.get(idCanale).add(tipoVersamento);
                    }
                });
        return map;
    }


    /**
     * @param pspCode Code of the payment service provider
     * @return the PSP record from DB if Exists
     */
    private Psp getPspIfExists(String pspCode) {
        return pspRepository.findByIdPsp(pspCode)
                .orElseThrow(() -> new AppException(AppError.PSP_NOT_FOUND, pspCode));
    }

    /**
     * Maps a list of PspCanaleTipoVersamento into a list of PspChannel
     *
     * @param pspCanaleTipoVersamentoList list of PspCanaleTipoVersamento from DB
     * @param channelPaymentType                      the data structure with the PaymentTypeCode associated at one Channel
     * @return the list of {@link PspChannel}
     */
    private List<PspChannel> getChannelsList(List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList, Map<String, Set<String>> channelPaymentType) {
        return pspCanaleTipoVersamentoList.stream()
                .filter(Objects::nonNull)
                .map(elem -> {
                    PspChannel result = modelMapper.map(elem, PspChannel.class);
                    // the mapping of PaymentTypeList is custom
                    result.setPaymentTypeList(getPaymentTypeList(channelPaymentType, result.getChannelCode()));
                    return result;
                })
                .distinct()
                .collect(Collectors.toList());
    }


    /**
     * @param channelPaymentType      the data structure with the PaymentTypeCode associated at one Channel
     * @param channelCode the channel code key of the data structure
     * @return a list of PaymentTypeCode get from data structure
     */
    private List<it.pagopa.pagopa.apiconfig.model.psp.Service.PaymentTypeCode> getPaymentTypeList(Map<String, Set<String>> channelPaymentType, String channelCode) {
        return channelPaymentType.get(channelCode)
                .stream()
                .map(it.pagopa.pagopa.apiconfig.model.psp.Service.PaymentTypeCode::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * Maps PSP objects stored in the DB in a List of PaymentServiceProvider
     *
     * @param page page of PSP returned from the database
     * @return a list of {@link PaymentServiceProvider}.
     */
    private List<PaymentServiceProvider> getPaymentServiceProviderList(Page<Psp> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, PaymentServiceProvider.class))
                .collect(Collectors.toList());
    }
}
