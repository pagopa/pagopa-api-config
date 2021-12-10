package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.ElencoServizi;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
import it.pagopa.pagopa.apiconfig.model.psp.Services;
import it.pagopa.pagopa.apiconfig.repository.ElencoServiziRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServicesService {
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ElencoServiziRepository elencoServiziRepository;


    public Services getServices(Integer limit, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        // TODO add filter
        Page<ElencoServizi> page = elencoServiziRepository.findAll(pageable);
        return Services.builder()
                .servicesList(getServicesList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    private List<Service> getServicesList(Page<ElencoServizi> page) {
        return page.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, Service.class))
                .collect(Collectors.toList());
    }
}
