package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CounterpartTable;
import it.pagopa.pagopa.apiconfig.model.CounterpartTables;
import it.pagopa.pagopa.apiconfig.repository.InformativePaMasterRepository;
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
public class CounterpartService {

    @Autowired
    InformativePaMasterRepository informativePaMasterRepository;

    @Autowired
    ModelMapper modelMapper;

    public CounterpartTables getCounterpartTables(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<InformativePaMaster> page = informativePaMasterRepository.findAll(pageable);
        return CounterpartTables.builder()
                .counterpartTableList(getCounterpartList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public byte[] getCounterpartTable(@NotNull String id, @NotNull String creditorInstitutionCode) {
        Optional<InformativePaMaster> result = informativePaMasterRepository.findByIdInformativaPaAndFkPa_IdDominio(id, creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Counterpart Table not found", "No Counterpart Table found with the provided IDs");
        }
        return result.get().getFkBinaryFile().getFileContent();
    }


    /**
     * Maps InformativePaMaster objects stored in the DB in a List of CounterpartTable
     *
     * @param page page of {@link InformativePaMaster} returned from the database
     * @return a list of {@link CounterpartTable}.
     */
    private List<CounterpartTable> getCounterpartList(Page<InformativePaMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, CounterpartTable.class))
                .collect(Collectors.toList());
    }
}
