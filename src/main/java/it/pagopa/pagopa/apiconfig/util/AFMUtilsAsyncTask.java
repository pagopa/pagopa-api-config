package it.pagopa.pagopa.apiconfig.util;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import it.gov.pagopa.apiconfig.starter.entity.Canali;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMaster;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.ServiceAmountCosmos;
import it.gov.pagopa.apiconfig.starter.repository.CdiMasterValidRepository;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.afm.CdiCosmos;
import it.pagopa.pagopa.apiconfig.model.afm.CdiDetailCosmos;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AFMUtilsAsyncTask {
  @Autowired private CdiMasterValidRepository cdiMasterValidRepository;

  @Value("${service.utils.subscriptionKey}")
  private String afmUtilsSubscriptionKey;

  @Autowired private ModelMapper modelMapper;

  private AFMUtilsClient afmUtilsClient;

  public AFMUtilsAsyncTask(@Value("${service.utils.host}") Optional<String> optAfmUtilsHost) {
    this.afmUtilsClient =
        Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(AFMUtilsClient.class, optAfmUtilsHost.orElse(""));
  }

  @Transactional(readOnly = true)
  public boolean executeSync() {
    int currentPage = 0;
    Pageable pageable = PageRequest.of(currentPage, 10);
    Page<CdiMasterValid> pagedCdiMaster = cdiMasterValidRepository.findAll(pageable);
    while (currentPage < pagedCdiMaster.getTotalPages()) {
      var result =
          pagedCdiMaster
              .get()
              .filter(Objects::nonNull)
              .map(elem -> modelMapper.map(elem, CdiMaster.class))
              .map(this::mapToCosmosEntity)
              .collect(Collectors.toList());
      CompletableFuture.runAsync(() -> afmUtilsTrigger(result));
      currentPage += 1;
      pageable = PageRequest.of(currentPage, 10);
      pagedCdiMaster = cdiMasterValidRepository.findAll(pageable);
    }
    return true;
  }

  public boolean executeSync(CdiMaster master) {
    afmUtilsTrigger(List.of(mapToCosmosEntity(master)));
    return true;
  }

  private CdiCosmos mapToCosmosEntity(CdiMaster master) {
    if (master.getCdiDetail() == null) {
      throw new AppException(AppError.CDI_DETAILS_NOT_FOUND, master.getIdInformativaPsp());
    }
    var cdiDetails =
        master.getCdiDetail().stream()
            .filter(Objects::nonNull)
            .map(this::mapDetails)
            .collect(Collectors.toList());
    return CdiCosmos.builder()
        .id(master.getId().toString())
        .idPsp(master.getFkPsp().getIdPsp())
        .abi(master.getFkPsp().getAbi())
        .idCdi(master.getIdInformativaPsp())
        .cdiStatus("NEW")
        .digitalStamp(master.getMarcaBolloDigitale())
        .validityDateFrom(
            master.getDataInizioValidita() != null
                ? master.getDataInizioValidita().toLocalDateTime().toLocalDate().toString()
                : null)
        .details(cdiDetails)
        .build();
  }

  private CdiDetailCosmos mapDetails(@NotNull CdiDetail detail) {
    @NotNull
    Canali canale = detail.getFkPspCanaleTipoVersamento().getCanaleTipoVersamento().getCanale();
    return CdiDetailCosmos.builder()
        .idChannel(canale.getIdCanale())
        .name(detail.getNomeServizio())
        .description(getDescription(detail))
        .channelApp(detail.getCanaleApp() == 1L)
        .paymentType(
            detail
                .getFkPspCanaleTipoVersamento()
                .getCanaleTipoVersamento()
                .getTipoVersamento()
                .getTipoVersamento())
        .idBrokerPsp(canale.getFkIntermediarioPsp().getIdIntermediarioPsp())
        .channelCardsCart(
            canale.getFkCanaliNodo() != null ? canale.getFkCanaliNodo().getCarrelloCarte() : null)
        .serviceAmount(
            detail.getCdiFasciaCostoServizio().stream()
                .map(
                    elem ->
                        ServiceAmountCosmos.builder()
                            .minPaymentAmount((int) (elem.getImportoMinimo() * 100))
                            .maxPaymentAmount((int) (elem.getImportoMassimo() * 100))
                            .paymentAmount((int) (elem.getCostoFisso() * 100))
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  private static String getDescription(@NotNull CdiDetail detail) {
    return detail.getCdiInformazioniServizio().stream()
        .filter(item -> "IT".equals(item.getCodiceLingua()))
        .findFirst()
        .map(CdiInformazioniServizio::getDescrizioneServizio)
        .orElse("");
  }

  private void afmUtilsTrigger(List<CdiCosmos> cdis) {
    try {
      afmUtilsClient.syncPaymentTypes(afmUtilsSubscriptionKey, cdis);
    } catch (Exception e) {
      String cdiList =
          cdis.stream().map(CdiCosmos::getIdCdi).collect(Collectors.joining(", ", "{", "}"));
      log.error("Problem to sync cdis: " + cdiList, e);
    }
  }
}
