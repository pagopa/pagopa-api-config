package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPsp;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPsp;
import it.pagopa.pagopa.apiconfig.model.psp.BrokerPspDetails;
import it.pagopa.pagopa.apiconfig.model.psp.BrokersPsp;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class BrokersPspService {

    @Autowired
    IntermediariPspRepository intermediariPspRepository;

    @Autowired
    ModelMapper modelMapper;


    public BrokersPsp getBrokersPsp(@NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, CommonUtil.getSort(filterAndOrder));
        var filters = CommonUtil.getFilters(IntermediariPsp.builder()
                .idIntermediarioPsp(filterAndOrder.getFilter().getCode())
                .codiceIntermediario(filterAndOrder.getFilter().getName())
                .build());
        Page<IntermediariPsp> page = intermediariPspRepository.findAll(filters, pageable);
        return BrokersPsp.builder()
                .brokerPspList(getBrokerPspList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public BrokerPspDetails getBrokerPsp(@NotBlank String brokerPspCode) {
        IntermediariPsp intermediariPsp = getIntermediariPspIfExists(brokerPspCode);
        return modelMapper.map(intermediariPsp, BrokerPspDetails.class);
    }

    public BrokerPspDetails createBrokerPsp(@NotNull BrokerPspDetails brokerPspDetails) {
        if (intermediariPspRepository.findByIdIntermediarioPsp(brokerPspDetails.getBrokerPspCode()).isPresent()) {
            throw new AppException(AppError.BROKER_CONFLICT, brokerPspDetails.getBrokerPspCode());
        }
        IntermediariPsp intermediariPsp = modelMapper.map(brokerPspDetails, IntermediariPsp.class);
        intermediariPspRepository.save(intermediariPsp);
        return brokerPspDetails;
    }


    public BrokerPspDetails updateBrokerPsp(@NotBlank String brokerPspCode, @NotNull BrokerPspDetails brokerPspDetails) {
        Long objId = getIntermediariPspIfExists(brokerPspCode).getObjId();
        IntermediariPsp intermediariPsp = modelMapper.map(brokerPspDetails, IntermediariPsp.class)
                .toBuilder()
                .objId(objId)
                .build();
        intermediariPspRepository.save(intermediariPsp);
        return brokerPspDetails;
    }

    public void deleteBrokerPsp(@NotBlank String brokerPspCode) {
        IntermediariPsp intermediariPsp = getIntermediariPspIfExists(brokerPspCode);
        intermediariPspRepository.delete(intermediariPsp);
    }

    /**
     * @param brokerPspCode code of the broker PSP
     * @return search on DB using the {@code brokerPspCode} and return the IntermediariPSP if it is present
     * @throws AppException if not found
     */
    private IntermediariPsp getIntermediariPspIfExists(String brokerPspCode) {
        return intermediariPspRepository.findByIdIntermediarioPsp(brokerPspCode)
                .orElseThrow(() -> new AppException(AppError.BROKER_PSP_NOT_FOUND, brokerPspCode));
    }

    /**
     * Maps IntermediariPsp objects stored in the DB in a List of BrokerPsp
     *
     * @param page page of {@link IntermediariPsp} returned from the database
     * @return a list of {@link BrokerPsp}.
     */
    private List<BrokerPsp> getBrokerPspList(Page<IntermediariPsp> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, BrokerPsp.class))
                .collect(Collectors.toList());
    }
}
