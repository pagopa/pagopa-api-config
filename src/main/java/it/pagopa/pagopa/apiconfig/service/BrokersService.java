package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.Broker;
import it.pagopa.pagopa.apiconfig.model.BrokerDetails;
import it.pagopa.pagopa.apiconfig.model.Brokers;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrokersService {

    @Autowired
    IntermediariPaRepository intermediariPaRepository;

    @Autowired
    ModelMapper modelMapper;

    public Brokers getBrokers(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<IntermediariPa> page = intermediariPaRepository.findAll(pageable);
        return Brokers.builder()
                .brokerList(getBrokerList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public BrokerDetails getBroker(String brokerCode) {
        Optional<IntermediariPa> result = intermediariPaRepository.findByCodiceIntermediario(brokerCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Broker not found", "No broker found with the provided code");
        }
        IntermediariPa intermediariPa = result.get();
        return modelMapper.map(intermediariPa, BrokerDetails.class);
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
