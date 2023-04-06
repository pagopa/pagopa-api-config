package it.gov.pagopa.apiconfig.service;

import static it.gov.pagopa.apiconfig.util.CommonUtil.getSort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import it.gov.pagopa.apiconfig.exception.AppError;
import it.gov.pagopa.apiconfig.exception.AppException;
import it.gov.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviderView;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.gov.pagopa.apiconfig.model.psp.PaymentServiceProvidersView;
import it.gov.pagopa.apiconfig.model.psp.PspChannel;
import it.gov.pagopa.apiconfig.model.psp.PspChannelCode;
import it.gov.pagopa.apiconfig.model.psp.PspChannelList;
import it.gov.pagopa.apiconfig.model.psp.PspChannelPaymentTypes;
import it.gov.pagopa.apiconfig.specification.PspSpecification;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.repository.CanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.CanaliRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspCanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspRepository;
import it.gov.pagopa.apiconfig.starter.repository.TipiVersamentoRepository;
import it.gov.pagopa.apiconfig.util.CommonUtil;

@Service
@Validated
@Transactional
public class PspService {
  @Autowired PspRepository pspRepository;

  @Autowired ModelMapper modelMapper;

  @Autowired PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;

  @Autowired CanaliRepository canaliRepository;

  @Autowired TipiVersamentoRepository tipiVersamentoRepository;

  @Autowired CanaleTipoVersamentoRepository canaleTipoVersamentoRepository;

  public PaymentServiceProviders getPaymentServiceProviders(
      @NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
    Pageable pageable = PageRequest.of(pageNumber, limit, getSort(filterAndOrder));
    var filters =
        CommonUtil.getFilters(
            Psp.builder()
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

  public PaymentServiceProviderDetails createPaymentServiceProvider(
      PaymentServiceProviderDetails paymentServiceProviderDetails) {
    validateInput(paymentServiceProviderDetails);

    if (pspRepository.findByIdPsp(paymentServiceProviderDetails.getPspCode()).isPresent()) {
      throw new AppException(AppError.PSP_CONFLICT, paymentServiceProviderDetails.getPspCode());
    }
    var psp = modelMapper.map(paymentServiceProviderDetails, Psp.class);
    var result = pspRepository.save(psp);
    return modelMapper.map(result, PaymentServiceProviderDetails.class);
  }

  public PaymentServiceProviderDetails updatePaymentServiceProvider(
      String pspCode, PaymentServiceProviderDetails paymentServiceProviderDetails) {
    var objId = getPspIfExists(pspCode).getObjId();
    var psp =
        modelMapper.map(paymentServiceProviderDetails, Psp.class).toBuilder().objId(objId).build();
    pspRepository.save(psp);
    return paymentServiceProviderDetails;
  }

  public void deletePaymentServiceProvider(String pspCode) {
    var psp = getPspIfExists(pspCode);
    pspRepository.delete(psp);
  }

  public PspChannelList getPaymentServiceProvidersChannels(@NotBlank String pspCode) {
    Psp psp = getPspIfExists(pspCode);
    List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList =
        pspCanaleTipoVersamentoRepository.findByFkPsp(psp.getObjId());
    // data structure useful for mapping
    Map<String, Set<String>> channelPaymentType =
        pspCanaleTipoVersamentotoMap(pspCanaleTipoVersamentoList);
    return PspChannelList.builder()
        .channelsList(getChannelsList(pspCanaleTipoVersamentoList, channelPaymentType))
        .build();
  }

  public PspChannelCode createPaymentServiceProvidersChannels(
      String pspCode, PspChannelCode pspChannelCode) {
    var psp = getPspIfExists(pspCode);
    var canale = getChannelIfExists(pspChannelCode.getChannelCode());

    if (pspChannelCode.getPaymentTypeList().isEmpty()) {
      throw new AppException(
          AppError.RELATION_CHANNEL_BAD_REQUEST, pspCode, pspChannelCode.getChannelCode());
    }

    // foreach payment type save a record in pspCanaleTipoVersamento
    for (String elem : pspChannelCode.getPaymentTypeList()) {
      savePspChannelRelation(psp, canale, elem);
    }
    return pspChannelCode;
  }

  public PspChannelPaymentTypes updatePaymentServiceProvidersChannels(
      String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes) {
    var psp = getPspIfExists(pspCode);
    var canale = getChannelIfExists(channelCode);

    if (pspChannelPaymentTypes.getPaymentTypeList().isEmpty()) {
      throw new AppException(AppError.RELATION_CHANNEL_BAD_REQUEST, pspCode, channelCode);
    }

    pspCanaleTipoVersamentoRepository.deleteAll(
        pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            psp.getObjId(), canale.getId()));

    // foreach payment type save a record in pspCanaleTipoVersamento
    for (String elem : pspChannelPaymentTypes.getPaymentTypeList()) {
      savePspChannelRelation(psp, canale, elem);
    }
    return pspChannelPaymentTypes;
  }

  public void deletePaymentServiceProvidersChannels(String pspCode, String channelCode) {
    var psp = getPspIfExists(pspCode);
    var canale = getChannelIfExists(channelCode);

    pspCanaleTipoVersamentoRepository.deleteAll(
        pspCanaleTipoVersamentoRepository.findByFkPspAndCanaleTipoVersamento_FkCanale(
            psp.getObjId(), canale.getId()));
  }

  /**
   * @param pspCanaleTipoVersamentoList list of record of PspCanaleTipoVersamento from DB
   * @return the data structure with the PaymentTypeCode associated at one Channel
   */
  private Map<String, Set<String>> pspCanaleTipoVersamentotoMap(
      List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList) {
    Map<String, Set<String>> map = new HashMap<>();
    pspCanaleTipoVersamentoList.forEach(
        elem -> {
          String idCanale = elem.getCanaleTipoVersamento().getCanale().getIdCanale();
          String tipoVersamento =
              elem.getCanaleTipoVersamento().getTipoVersamento().getTipoVersamento();
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
    return pspRepository
        .findByIdPsp(pspCode)
        .orElseThrow(() -> new AppException(AppError.PSP_NOT_FOUND, pspCode));
  }

  /**
   * Maps a list of PspCanaleTipoVersamento into a list of PspChannel
   *
   * @param pspCanaleTipoVersamentoList list of PspCanaleTipoVersamento from DB
   * @param channelPaymentType the data structure with the PaymentTypeCode associated at one Channel
   * @return the list of {@link PspChannel}
   */
  private List<PspChannel> getChannelsList(
      List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList,
      Map<String, Set<String>> channelPaymentType) {
    return pspCanaleTipoVersamentoList.stream()
        .filter(Objects::nonNull)
        .map(
            elem -> {
              PspChannel result = modelMapper.map(elem, PspChannel.class);
              // the mapping of PaymentTypeList is custom
              result.setPaymentTypeList(
                  getPaymentTypeList(channelPaymentType, result.getChannelCode()));
              return result;
            })
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * @param channelPaymentType the data structure with the PaymentTypeCode associated at one Channel
   * @param channelCode the channel code key of the data structure
   * @return a list of PaymentTypeCode get from data structure
   */
  private List<String> getPaymentTypeList(
      Map<String, Set<String>> channelPaymentType, String channelCode) {
    return new ArrayList<>(channelPaymentType.get(channelCode));
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
   * Maps PSP objects stored in the DB in a List of PaymentServiceProviderView
   *
   * @param page page of PSP returned from the database
   * @return a list of {@link PaymentServiceProviderView}.
   */
  private List<PaymentServiceProviderView> getPaymentServiceProviderViewList(Page<Psp> page) {
    return page.stream()
        .map(elem -> modelMapper.map(elem, PaymentServiceProviderView.class))
        .collect(Collectors.toList());
  }

  /**
   * @param channelCode code of the channel
   * @return search on DB using the {@code channelCode} and return the Canali if it is present
   * @throws AppException if not found
   */
  private Canali getChannelIfExists(String channelCode) throws AppException {
    return canaliRepository
        .findByIdCanale(channelCode)
        .orElseThrow(() -> new AppException(AppError.CHANNEL_NOT_FOUND, channelCode));
  }

  /**
   * Save a record in pspCanaleTipoVersamento
   *
   * @param psp PSP entity
   * @param channel channel entity
   * @param paymentTypeCode paymentTypeCode entity
   */
  private void savePspChannelRelation(Psp psp, Canali channel, String paymentTypeCode) {
    var tipoVersamento =
        tipiVersamentoRepository
            .findByTipoVersamento(paymentTypeCode)
            .orElseThrow(() -> new AppException(AppError.PAYMENT_TYPE_NOT_FOUND, paymentTypeCode));

    var canaleTipoVersamento =
        canaleTipoVersamentoRepository
            .findByFkCanaleAndFkTipoVersamento(channel.getId(), tipoVersamento.getId())
            .orElseThrow(
                () ->
                    new AppException(
                        AppError.CHANNEL_PAYMENT_TYPE_NOT_FOUND,
                        channel.getIdCanale(),
                        paymentTypeCode));

    // check if the relation already exists
    if (pspCanaleTipoVersamentoRepository
        .findByFkPspAndCanaleTipoVersamento_FkCanaleAndCanaleTipoVersamento_FkTipoVersamento(
            psp.getObjId(), channel.getId(), tipoVersamento.getId())
        .isPresent()) {
      throw new AppException(
          AppError.RELATION_CHANNEL_CONFLICT,
          psp.getIdPsp(),
          channel.getIdCanale(),
          paymentTypeCode);
    }

    // save pspCanaleTipoVersamento
    var entity =
        PspCanaleTipoVersamento.builder()
            .psp(psp)
            .canaleTipoVersamento(canaleTipoVersamento)
            .build();
    pspCanaleTipoVersamentoRepository.save(entity);
  }

  /**
   * check if pspCode match the pattern
   *
   * @param paymentServiceProviderDetails PSP details
   */
  private static void validateInput(PaymentServiceProviderDetails paymentServiceProviderDetails) {
    boolean match = Pattern.matches("[A-Z0-9]{6,14}", paymentServiceProviderDetails.getPspCode());
    if (!match) {
      throw new ValidationException("pspCode doesn't match the pattern [A-Z0-9]{6,14}");
    }
  }

  public PaymentServiceProvidersView getPaymentServiceProvidersView(@Positive Integer limit, @PositiveOrZero Integer pageNumber,
      String pspCode, String pspBrokerCode, String channelCode, String paymentType, String paymentModel) {
    Pageable pageable = PageRequest.of(pageNumber, limit);
    Page<Psp> page =
        pspRepository.findAll(
            PspSpecification.filterViewPspChannelBroker(pspCode, pspBrokerCode, channelCode, paymentType, paymentModel),
            pageable);
    return PaymentServiceProvidersView.builder()
        .paymentServiceProviderList(getPaymentServiceProviderViewList(page))
        .pageInfo(CommonUtil.buildPageInfo(page))
        .build();
  }
}
