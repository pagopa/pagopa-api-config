package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannel;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelCode;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelList;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.pagopa.pagopa.apiconfig.repository.CanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
import it.pagopa.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getSort;


@Service
@Validated
public class PspService {
    @Autowired
    PspRepository pspRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    @Autowired
    CanaliRepository canaliRepository;

    @Autowired
    TipiVersamentoRepository tipiVersamentoRepository;

    @Autowired
    CanaleTipoVersamentoRepository canaleTipoVersamentoRepository;


    public PaymentServiceProviders getPaymentServiceProviders(@NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, getSort(filterAndOrder));
        var filters = CommonUtil.getFilters(Psp.builder()
                .idPsp(filterAndOrder.getFilter().getCode())
                .ragioneSociale(filterAndOrder.getFilter().getName())
                .build());
        Page<Psp> page = pspRepository.findAll(filters, pageable);
        return PaymentServiceProviders.builder()
                .paymentServiceProviderList(getPaymentServiceProviderList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public PaymentServiceProviderDetails getPaymentServiceProvider(@NotNull String pspCode) {
        Psp psp = getPspIfExists(pspCode);
        return modelMapper.map(psp, PaymentServiceProviderDetails.class);
    }


    public PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails) {
        if (pspRepository.findByIdPsp(paymentServiceProviderDetails.getPspCode()).isPresent()) {
            throw new AppException(AppError.PSP_CONFLICT, paymentServiceProviderDetails.getPspCode());
        }
        var psp = modelMapper.map(paymentServiceProviderDetails, Psp.class);
        var result = pspRepository.save(psp);
        return modelMapper.map(result, PaymentServiceProviderDetails.class);
    }

    public PaymentServiceProviderDetails updatePaymentServiceProvider(String pspCode, PaymentServiceProviderDetails paymentServiceProviderDetails) {
        var objId = getPspIfExists(pspCode).getObjId();
        var psp = modelMapper.map(paymentServiceProviderDetails, Psp.class)
                .toBuilder()
                .objId(objId)
                .build();
        pspRepository.save(psp);
        return paymentServiceProviderDetails;
    }

    public void deletePaymentServiceProvider(String pspCode) {
        var psp = getPspIfExists(pspCode);
        pspRepository.delete(psp);
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PspChannelCode createPaymentServiceProvidersChannels(String pspCode, PspChannelCode pspChannelCode) {
        var psp = getPspIfExists(pspCode);
        var canale = getChannelIfExists(pspChannelCode.getChannelCode());

        // foreach payment type save a record in pspCanaleTipoVersamento
        for (it.pagopa.pagopa.apiconfig.model.psp.Service.PaymentTypeCode elem : pspChannelCode.getPaymentTypeList()) {
            savePspChannelRelation(psp, canale, elem);
        }
        return pspChannelCode;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
        var psp = getPspIfExists(pspCode);
        var canale = getChannelIfExists(channelCode);

        pspCanaleTipoVersamentoRepository.deleteAll(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(psp.getObjId(), canale.getId()));

        // foreach payment type save a record in pspCanaleTipoVersamento
        for (it.pagopa.pagopa.apiconfig.model.psp.Service.PaymentTypeCode elem : pspChannelPaymentTypes.getPaymentTypeList()) {
            savePspChannelRelation(psp, canale, elem);
        }
        return pspChannelPaymentTypes;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deletePaymentServiceProvidersChannels(String pspCode, String channelCode) {
        var psp = getPspIfExists(pspCode);
        var canale = getChannelIfExists(channelCode);

        pspCanaleTipoVersamentoRepository.deleteAll(pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(psp.getObjId(), canale.getId()));
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
     * @param channelPaymentType          the data structure with the PaymentTypeCode associated at one Channel
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
     * @param channelPaymentType the data structure with the PaymentTypeCode associated at one Channel
     * @param channelCode        the channel code key of the data structure
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


    /**
     * @param channelCode code of the channel
     * @return search on DB using the {@code channelCode} and return the Canali if it is present
     * @throws AppException if not found
     */
    private Canali getChannelIfExists(String channelCode) throws AppException {
        return canaliRepository.findByIdCanale(channelCode)
                .orElseThrow(() -> new AppException(AppError.CHANNEL_NOT_FOUND, channelCode));
    }

    /**
     * Save a record in pspCanaleTipoVersamento
     *
     * @param psp             PSP entity
     * @param channel         channel entity
     * @param paymentTypeCode paymentTypeCode entity
     */
    private void savePspChannelRelation(Psp psp, Canali channel, it.pagopa.pagopa.apiconfig.model.psp.Service.PaymentTypeCode paymentTypeCode) {
        var tipoVersamento = tipiVersamentoRepository.findByTipoVersamento(paymentTypeCode.name())
                .orElseThrow(() -> new AppException(AppError.PAYMENT_TYPE_NOT_FOUND, paymentTypeCode.name()));

        var canaleTipoVersamento = canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(channel.getId(), tipoVersamento.getId())
                .orElseThrow(() -> new AppException(AppError.CHANNEL_PAYMENT_TYPE_NOT_FOUND, channel.getIdCanale(), paymentTypeCode.name()));

        // check if the relation already exists
        if (pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanaleAndCanaleTipoVersamento_FkTipoVersamento(psp.getObjId(), channel.getId(), tipoVersamento.getId()).isPresent()) {
            throw new AppException(AppError.RELATION_CHANNEL_CONFLICT, psp.getIdPsp(), channel.getIdCanale(), paymentTypeCode.name());
        }

        // save pspCanaleTipoVersamento
        var entity = PspCanaleTipoVersamento.builder()
                .psp(psp)
                .canaleTipoVersamento(canaleTipoVersamento)
                .build();
        pspCanaleTipoVersamentoRepository.save(entity);
    }

}
