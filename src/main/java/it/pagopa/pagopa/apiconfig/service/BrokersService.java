package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Broker;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Brokers;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class BrokersService {

  @Autowired IntermediariPaRepository intermediariPaRepository;

  @Autowired ModelMapper modelMapper;

  public Brokers getBrokers(
      @NotNull Integer limit, @NotNull Integer pageNumber, FilterAndOrder filterAndOrder) {
    Pageable pageable = PageRequest.of(pageNumber, limit, CommonUtil.getSort(filterAndOrder));
    var filters =
        CommonUtil.getFilters(
            IntermediariPa.builder()
                .idIntermediarioPa(filterAndOrder.getFilter().getCode())
                .codiceIntermediario(filterAndOrder.getFilter().getName())
                .build());
    Page<IntermediariPa> page = intermediariPaRepository.findAll(filters, pageable);
    return Brokers.builder()
        .brokerList(getBrokerList(page))
        .pageInfo(CommonUtil.buildPageInfo(page))
        .build();
  }

  public BrokerDetails getBroker(@NotBlank String brokerCode) {
    IntermediariPa intermediariPa = getIntermediarioIfExists(brokerCode);
    return modelMapper.map(intermediariPa, BrokerDetails.class);
  }

  public BrokerDetails createBroker(BrokerDetails brokerDetails) {
    if (intermediariPaRepository
        .findByIdIntermediarioPa(brokerDetails.getBrokerCode())
        .isPresent()) {
      throw new AppException(AppError.BROKER_CONFLICT, brokerDetails.getBrokerCode());
    }
    IntermediariPa intermediariPa = modelMapper.map(brokerDetails, IntermediariPa.class);
    IntermediariPa result = intermediariPaRepository.save(intermediariPa);
    return modelMapper.map(result, BrokerDetails.class);
  }

  public BrokerDetails updateBroker(String brokerCode, BrokerDetails brokerDetails) {
    Long objId = getIntermediarioIfExists(brokerCode).getObjId();
    IntermediariPa intermediariPa =
        modelMapper.map(brokerDetails, IntermediariPa.class).toBuilder().objId(objId).build();
    IntermediariPa result = intermediariPaRepository.save(intermediariPa);
    return modelMapper.map(result, BrokerDetails.class);
  }

  public void deleteBroker(String brokerCode) {
    IntermediariPa intermediariPa = getIntermediarioIfExists(brokerCode);
    intermediariPaRepository.delete(intermediariPa);
  }

  /**
   * @param brokerCode code of the broker
   * @return search on DB using the {@code brokerCode} and return the IntermediariPa if it is
   *     present
   * @throws AppException if not found
   */
  private IntermediariPa getIntermediarioIfExists(String brokerCode) {
    Optional<IntermediariPa> result = intermediariPaRepository.findByIdIntermediarioPa(brokerCode);
    if (result.isEmpty()) {
      throw new AppException(AppError.BROKER_NOT_FOUND, brokerCode);
    }
    return result.get();
  }

  /**
   * Maps IntermediariPa objects stored in the DB in a List of Broker
   *
   * @param page page of {@link IntermediariPa} returned from the database
   * @return a list of {@link Broker}.
   */
  private List<Broker> getBrokerList(Page<IntermediariPa> page) {
    return page.stream()
        .map(elem -> modelMapper.map(elem, Broker.class))
        .collect(Collectors.toList());
  }
}
