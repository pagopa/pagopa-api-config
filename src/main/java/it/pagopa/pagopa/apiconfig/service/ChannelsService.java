package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.CanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.entity.Canali;
import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.entity.PspCanaleTipoVersamento;
import it.pagopa.pagopa.apiconfig.entity.TipiVersamento;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.PaymentType;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.Channel;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelDetails;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelPsp;
import it.pagopa.pagopa.apiconfig.model.psp.ChannelPspList;
import it.pagopa.pagopa.apiconfig.model.psp.Channels;
import it.pagopa.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.pagopa.pagopa.apiconfig.repository.CanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.CanaliRepository;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.pagopa.pagopa.apiconfig.repository.WfespPluginConfRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.getSort;

@Service
@Validated
public class ChannelsService {

    @Autowired
    CanaliRepository canaliRepository;

    @Autowired
    IntermediariPspRepository intermediariPspRepository;

    @Autowired
    WfespPluginConfRepository wfespPluginConfRepository;

    @Autowired
    CanaleTipoVersamentoRepository canaleTipoVersamentoRepository;

    @Autowired
    TipiVersamentoRepository tipiVersamentoRepository;

    @Autowired
    PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Channels getChannels(@NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, getSort(filterAndOrder));
        var filters = CommonUtil.getFilters(Canali.builder()
                .idCanale(filterAndOrder.getFilter().getCode())
                .build());
        Page<Canali> page = canaliRepository.findAll(filters, pageable);
        return Channels.builder()
                .channelList(getChannelList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public ChannelDetails getChannel(@NotBlank String channelCode) {
        Canali canali = getCanaliIfExists(channelCode);
        return modelMapper.map(canali, ChannelDetails.class);
    }

    public ChannelDetails createChannel(ChannelDetails channelDetails) {
        if (canaliRepository.findByIdCanale(channelDetails.getChannelCode()).isPresent()) {
            throw new AppException(AppError.CHANNEL_CONFLICT, channelDetails.getChannelCode());
        }

        // add info for model mapper
        setInfoMapper(channelDetails);

        // convert and save
        var entity = modelMapper.map(channelDetails, Canali.class);
        canaliRepository.save(entity);
        return channelDetails;
    }

    public ChannelDetails updateChannel(String channelCode, ChannelDetails channelDetails) {
        Long objId = getCanaliIfExists(channelCode).getId();

        // add info for model mapper
        setInfoMapper(channelDetails);

        var entity = modelMapper.map(channelDetails, Canali.class)
                .toBuilder()
                .id(objId)
                .build();
        canaliRepository.save(entity);
        return channelDetails;
    }

    public void deleteChannel(String channelCode) {
        Canali canale = getCanaliIfExists(channelCode);
        try {
            canaliRepository.delete(canale);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(AppError.CHANNEL_PAYMENT_TYPE_FOUND, channelCode);
        }
    }

    public PspChannelPaymentTypes getPaymentTypes(@NotBlank String channelCode) {
        var channel = getCanaliIfExists(channelCode);
        var type = canaleTipoVersamentoRepository.findByFkCanale(channel.getId());
        return PspChannelPaymentTypes.builder()
                .paymentTypeList(getPaymentTypeList(type))
                .build();
    }

    public PspChannelPaymentTypes createPaymentType(@NotBlank String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
        // necessary to prevent 201 status code without at least one payment type specified
        if (pspChannelPaymentTypes.getPaymentTypeList().isEmpty()) {
            throw new AppException(AppError.PAYMENT_TYPE_BAD_REQUEST);
        }

        var channel = getCanaliIfExists(channelCode);
        // foreach type in the request...
        for (String type : pspChannelPaymentTypes.getPaymentTypeList()) {
            // ...search the type in DB
            var paymentType = getPaymentTypeIfExists(type);
            // check if already exists a relation Channel-PaymentType...
            if (canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(channel.getId(), paymentType.getId()).isPresent()) {
                throw new AppException(AppError.CHANNEL_PAYMENT_TYPE_CONFLICT, channel.getIdCanale(), paymentType.getTipoVersamento());
            }

            // ...if NOT exists, save the new relation
            var entity = CanaleTipoVersamento.builder()
                    .canale(channel)
                    .tipoVersamento(paymentType)
                    .build();
            canaleTipoVersamentoRepository.save(entity);
        }
        return getPaymentTypes(channelCode);
    }

    public void deletePaymentType(@NotBlank String channelCode, @NotBlank String paymentTypeCode) {
        var channel = getCanaliIfExists(channelCode);
        var paymentType = getPaymentTypeIfExists(paymentTypeCode);
        var result = getCanaleTipoVersamentoIfExists(channel, paymentType);
        canaleTipoVersamentoRepository.delete(result);
    }

    public ChannelPspList getChannelPaymentServiceProviders(String channelCode) {
        // get all the PSPs of the channel with relative payment types
        Map<Psp, List<PspCanaleTipoVersamento>> pspList = pspCanaleTipoVersamentoRepository.findByCanaleTipoVersamento_Canale_IdCanale(channelCode)
                .stream()
                .collect(Collectors.groupingBy(PspCanaleTipoVersamento::getPsp));

        List<ChannelPsp> result = new ArrayList<>();
        // map the PSPs to the response
        pspList.forEach((key, value) -> {
            // map relation entity to list of strings
            var tipiVersamento = mapPaymentType(value);

            // map PSP to ChannelPsp and add to result
            var channelPsp = mapChannelPsp(key, tipiVersamento);
            result.add(channelPsp);
        });

        return ChannelPspList.builder()
                .psp(result)
                .build();
    }

    /**
     * Map PSP and list of payment types to ChannelPsp
     *
     * @param psp            Psp entity
     * @param tipiVersamento list of payment types
     * @return ChannelPsp model
     */
    private ChannelPsp mapChannelPsp(Psp psp, List<String> tipiVersamento) {
        return ChannelPsp.builder()
                .pspCode(psp.getIdPsp())
                .businessName(psp.getRagioneSociale())
                .enabled(psp.getEnabled())
                .paymentTypeList(tipiVersamento)
                .build();
    }

    /**
     * Get the list of payment types as string from the etity
     *
     * @param list of entities
     * @return list of string
     */
    private List<String> mapPaymentType(List<PspCanaleTipoVersamento> list) {
        return list.stream()
                .map(PspCanaleTipoVersamento::getCanaleTipoVersamento)
                .map(elem -> elem.getTipoVersamento().getTipoVersamento())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * @param type code of TipiVersamento
     * @return find TipoVersamento if exists in DB
     */
    private TipiVersamento getPaymentTypeIfExists(String type) {
        return tipiVersamentoRepository.findByTipoVersamento(type)
                .orElseThrow(() -> new AppException(AppError.PAYMENT_TYPE_NOT_FOUND, type));
    }

    /**
     * @param channelCode code of the channel
     * @return search on DB using the {@code channelCode} and return the Canali if it is present
     * @throws AppException if not found
     */
    private Canali getCanaliIfExists(String channelCode) {
        return canaliRepository.findByIdCanale(channelCode)
                .orElseThrow(() -> new AppException(AppError.CHANNEL_NOT_FOUND, channelCode));
    }

    /**
     * Maps Canali objects stored in the DB in a List of Channel
     *
     * @param page page of {@link Canali} returned from the database
     * @return a list of {@link Channel}.
     */
    private List<Channel> getChannelList(Page<Canali> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Channel.class))
                .collect(Collectors.toList());
    }

    /**
     * Add info for model mapper
     *
     * @param channelDetails channel details form request
     */
    private void setInfoMapper(ChannelDetails channelDetails) {
        var intermediariPsp = intermediariPspRepository.findByIdIntermediarioPsp(channelDetails.getBrokerPspCode())
                .orElseThrow(() -> new AppException(AppError.BROKER_PSP_NOT_FOUND, channelDetails.getBrokerPspCode()));
        channelDetails.setFkIntermediarioPsp(intermediariPsp);

        if (channelDetails.getServPlugin() != null) {
            var wfespPluginConf = wfespPluginConfRepository.findByIdServPlugin(channelDetails.getServPlugin())
                    .orElseThrow(() -> new AppException(AppError.SERV_PLUGIN_NOT_FOUND, channelDetails.getServPlugin()));

            channelDetails.setFkWfespPluginConf(wfespPluginConf);
        }
    }

    /**
     * @param channel     an entity CANALI
     * @param paymentType an entity TIPI_VERSAMENTO
     * @return search on DB using the {@code channelCode} and {@code paymentType} if it is present
     * @throws AppException if not found
     */
    private CanaleTipoVersamento getCanaleTipoVersamentoIfExists(Canali channel, TipiVersamento paymentType) {
        return canaleTipoVersamentoRepository.findByFkCanaleAndFkTipoVersamento(channel.getId(), paymentType.getId())
                .orElseThrow(() -> new AppException(AppError.CHANNEL_PAYMENT_TYPE_NOT_FOUND, channel.getIdCanale(), paymentType.getTipoVersamento()));
    }

    /**
     * @param type list of CanaleTipoVersamento
     * @return map each element of the list in a PaymentTypeCode
     */
    private List<String> getPaymentTypeList(List<CanaleTipoVersamento> type) {
        return type.stream()
                .map(elem -> modelMapper.map(elem, PaymentType.class))
                .map(elem -> modelMapper.map(elem, String.class))
                .collect(Collectors.toList());
    }

}
