package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.ElencoServizi;
import it.pagopa.pagopa.apiconfig.model.psp.Service;
import it.pagopa.pagopa.apiconfig.model.psp.Services;
import it.pagopa.pagopa.apiconfig.repository.ElencoServiziRepository;
import it.pagopa.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Validated
@Transactional
public class ServicesService {
  @Autowired ModelMapper modelMapper;

  @Autowired ElencoServiziRepository elencoServiziRepository;

  @Autowired TipiVersamentoRepository tipiVersamentoRepository;

  public Services getServices(Integer limit, Integer pageNumber, Service.Filter filters) {
    Pageable pageable = PageRequest.of(pageNumber, limit);
    // filter only if is not null
    ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
    Example<ElencoServizi> query =
        Example.of(
            ElencoServizi.builder()
                .pspId(filters.getPspCode())
                .intmId(filters.getBrokerPspCode())
                .canaleId(filters.getChannelCode())
                .canaleModPag(filters.getPaymentMethodChannel())
                .tipoVersCod(filters.getPaymentTypeCode())
                .pspFlagBollo(filters.getPspFlagStamp())
                .canaleApp(filters.getChannelApp())
                .onUs(filters.getOnUs())
                .flagIo(filters.getFlagIo())
                .flussoId(filters.getFlowId())
                .importoMinimo(filters.getMinimumAmount())
                .importoMassimo(filters.getMaximumAmount())
                .codiceLingua(String.valueOf(filters.getLanguageCode()))
                .codiceConvenzione(filters.getConventionCode())
                .build(),
            matcher);
    Page<ElencoServizi> page = elencoServiziRepository.findAll(query, pageable);
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
