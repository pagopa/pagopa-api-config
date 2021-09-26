package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BrokersService {

    @Autowired
    IntermediariPaRepository intermediariPaRepository;

    public List<IntermediariPa> getBrokers() {
        List<IntermediariPa> result = intermediariPaRepository.findAll(Pageable.unpaged())
                .getContent();
        log.info(result.toString());
        return result;
    }
}
