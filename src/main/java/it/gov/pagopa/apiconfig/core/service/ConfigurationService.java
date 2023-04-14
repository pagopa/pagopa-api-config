package it.gov.pagopa.apiconfig.core.service;

import feign.Feign;
import feign.FeignException;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import it.gov.pagopa.apiconfig.core.client.AFMMarketplaceClient;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.afm.PaymentTypesCosmos;
import it.gov.pagopa.apiconfig.core.model.configuration.*;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.starter.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static it.gov.pagopa.apiconfig.core.util.Constants.HEADER_REQUEST_ID;

@Service
@Validated
@Transactional
@Slf4j
public class ConfigurationService {

  @Autowired private ConfigurationKeysRepository configurationKeysRepository;

  @Autowired private WfespPluginConfRepository wfespPluginConfRepository;

  @Autowired private PddRepository pddRepository;

  @Autowired private FtpServersRepository ftpServersRepository;

  @Autowired private TipiVersamentoRepository tipiVersamentoRepository;

  @Autowired private ModelMapper modelMapper;

  @Value("${service.marketplace.subscriptionKey}")
  private String afmMarketplaceSubscriptionKey;

  @Autowired
  HttpServletRequest httpServletRequest;

  private AFMMarketplaceClient afmMarketplaceClient;

  public ConfigurationService(
      @Value("${service.marketplace.host}") Optional<String> optAfmMarketplaceHost) {

    this.afmMarketplaceClient =
        Feign.builder()
            .encoder(new JacksonEncoder())
            .decoder(new JacksonDecoder())
            .target(AFMMarketplaceClient.class, optAfmMarketplaceHost.orElse(""));
  }

  public ConfigurationKeys getConfigurationKeys() {
    List<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys> configKeyList =
        configurationKeysRepository.findAll();
    return it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKeys.builder()
        .configurationKeyList(getConfigurationKeys(configKeyList))
        .build();
  }

  public ConfigurationKey getConfigurationKey(@NotNull String category, @NotNull String key) {
    it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys configKey =
        getConfigurationKeyIfExists(category, key);
    return modelMapper.map(configKey, ConfigurationKey.class);
  }

  public ConfigurationKey createConfigurationKey(ConfigurationKey configurationKey) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys> configKey =
        configurationKeysRepository.findByConfigCategoryAndConfigKey(
            configurationKey.getConfigCategory(), configurationKey.getConfigKey());
    if (configKey.isPresent()) {
      throw new AppException(
          AppError.CONFIGURATION_KEY_CONFLICT,
          configKey.get().getConfigCategory(),
          configKey.get().getConfigKey());
    }

    it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys configKeyEntity =
        modelMapper.map(
            configurationKey, it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys.class);
    configurationKeysRepository.save(configKeyEntity);
    return modelMapper.map(configKeyEntity, ConfigurationKey.class);
  }

  public ConfigurationKey updateConfigurationKey(
      String category, String key, ConfigurationKeyBase configurationKey) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys> configKey =
        configurationKeysRepository.findByConfigCategoryAndConfigKey(category, key);
    if (configKey.isEmpty()) {
      throw new AppException(AppError.CONFIGURATION_KEY_NOT_FOUND, category, key);
    }

    it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys configKeyEntity = configKey.get();
    configKeyEntity.setConfigValue(configurationKey.getConfigValue());
    configKeyEntity.setConfigDescription(configurationKey.getConfigDescription());
    configurationKeysRepository.save(configKeyEntity);

    return modelMapper.map(configKeyEntity, ConfigurationKey.class);
  }

  public void deleteConfigurationKey(String category, String key) {
    it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys configurationKey =
        getConfigurationKeyIfExists(category, key);
    configurationKeysRepository.delete(configurationKey);
  }

  public WfespPluginConfs getWfespPluginConfigurations() {
    List<it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf> list =
        wfespPluginConfRepository.findAll();
    return it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConfs.builder()
        .wfespPluginConfList(getWfespPluginConfList(list))
        .build();
  }

  public WfespPluginConf getWfespPluginConfiguration(@NotNull String idServPlugin) {
    it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf wfespPluginConf =
        getWfespPluginConfigurationIfExists(idServPlugin);
    return modelMapper.map(wfespPluginConf, WfespPluginConf.class);
  }

  public WfespPluginConf createWfespPluginConfiguration(WfespPluginConf wfespPluginConf) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf> wp =
        wfespPluginConfRepository.findByIdServPlugin(wfespPluginConf.getIdServPlugin());
    if (wp.isPresent()) {
      throw new AppException(
          AppError.CONFIGURATION_WFESP_PLUGIN_CONFLICT, wfespPluginConf.getIdServPlugin());
    }

    it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf wpEntity =
        modelMapper.map(wfespPluginConf, it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf.class);
    wfespPluginConfRepository.save(wpEntity);
    return modelMapper.map(wpEntity, WfespPluginConf.class);
  }

  public WfespPluginConf updateWfespPluginConfiguration(
      String idServPlugin, WfespPluginConfBase wfespPluginConf) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf> wp =
        wfespPluginConfRepository.findByIdServPlugin(idServPlugin);
    if (wp.isEmpty()) {
      throw new AppException(AppError.CONFIGURATION_WFESP_PLUGIN_NOT_FOUND, idServPlugin);
    }

    it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf wpEntity = wp.get();
    wpEntity.setIdServPlugin(idServPlugin);
    wpEntity.setIdBean(wfespPluginConf.getIdBean());
    wpEntity.setProfiloPagConstString(wfespPluginConf.getProfiloPagConstString());
    wpEntity.setProfiloPagSoapRule(wfespPluginConf.getProfiloPagSoapRule());
    wpEntity.setProfiloPagRptXpath(wfespPluginConf.getProfiloPagRptXpath());

    wfespPluginConfRepository.save(wpEntity);

    return modelMapper.map(wpEntity, WfespPluginConf.class);
  }

  public void deleteWfespPluginConfiguration(String idServPlugin) {
    it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf wp =
        getWfespPluginConfigurationIfExists(idServPlugin);
    wfespPluginConfRepository.delete(wp);
  }

  public Pdds getPdds() {
    List<it.gov.pagopa.apiconfig.starter.entity.Pdd> pddList = pddRepository.findAll();
    return it.gov.pagopa.apiconfig.core.model.configuration.Pdds.builder()
        .pddList(getPdds(pddList))
        .build();
  }

  public Pdd getPdd(@NotNull String idPdd) {
    it.gov.pagopa.apiconfig.starter.entity.Pdd pdd = getPddIfExists(idPdd);
    return modelMapper.map(pdd, Pdd.class);
  }

  public Pdd createPdd(Pdd pdd) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.Pdd> optPdd =
        pddRepository.findByIdPdd(pdd.getIdPdd());
    if (optPdd.isPresent()) {
      throw new AppException(AppError.PDD_CONFLICT, pdd.getIdPdd());
    }

    it.gov.pagopa.apiconfig.starter.entity.Pdd pddEntity =
        modelMapper.map(pdd, it.gov.pagopa.apiconfig.starter.entity.Pdd.class);
    pddRepository.save(pddEntity);
    return modelMapper.map(pddEntity, Pdd.class);
  }

  public Pdd updatePdd(String idPdd, PddBase pdd) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.Pdd> optPdd = pddRepository.findByIdPdd(idPdd);
    if (optPdd.isEmpty()) {
      throw new AppException(AppError.PDD_NOT_FOUND, idPdd);
    }
    it.gov.pagopa.apiconfig.starter.entity.Pdd updatedPdd = optPdd.get();
    updatedPdd.setEnabled(pdd.getEnabled());
    updatedPdd.setDescrizione(pdd.getDescription());
    updatedPdd.setIp(pdd.getIp());
    updatedPdd.setPorta(pdd.getPort());
    pddRepository.save(updatedPdd);

    return modelMapper.map(updatedPdd, Pdd.class);
  }

  public void deletePdd(String idPdd) {
    it.gov.pagopa.apiconfig.starter.entity.Pdd pdd = getPddIfExists(idPdd);
    pddRepository.delete(pdd);
  }

  public FtpServers getFtpServers() {
    List<it.gov.pagopa.apiconfig.starter.entity.FtpServers> ftpServerList =
        ftpServersRepository.findAll();
    return it.gov.pagopa.apiconfig.core.model.configuration.FtpServers.builder()
        .ftpServerList(getFtpServers(ftpServerList))
        .build();
  }

  public FtpServer getFtpServer(
      @NotNull String host, @NotNull Integer port, @NotNull String service) {
    it.gov.pagopa.apiconfig.starter.entity.FtpServers ftpServers =
        getFtpServerIfExists(host, port, service);
    return modelMapper.map(ftpServers, FtpServer.class);
  }

  public FtpServer createFtpServer(FtpServer ftpServer) {
    Optional<it.gov.pagopa.apiconfig.starter.entity.FtpServers> fs =
        ftpServersRepository.findByHostAndPortAndService(
            ftpServer.getHost(), ftpServer.getPort(), ftpServer.getService());
    if (fs.isPresent()) {
      throw new AppException(
          AppError.FTP_SERVER_CONFLICT,
          ftpServer.getHost(),
          ftpServer.getPort(),
          ftpServer.getService());
    }

    it.gov.pagopa.apiconfig.starter.entity.FtpServers ftpServerE =
        modelMapper.map(ftpServer, it.gov.pagopa.apiconfig.starter.entity.FtpServers.class);
    ftpServersRepository.save(ftpServerE);
    return modelMapper.map(ftpServerE, FtpServer.class);
  }

  public FtpServer updateFtpServer(String host, Integer port, String service, FtpServer ftpServer) {
    it.gov.pagopa.apiconfig.starter.entity.FtpServers ftpServerE =
        getFtpServerIfExists(host, port, service);

    it.gov.pagopa.apiconfig.starter.entity.FtpServers updatedFtpServer =
        modelMapper.map(ftpServer, it.gov.pagopa.apiconfig.starter.entity.FtpServers.class).toBuilder()
            .id(ftpServerE.getId())
            .build();
    ftpServersRepository.save(updatedFtpServer);

    return modelMapper.map(updatedFtpServer, FtpServer.class);
  }

  public void deleteFtpServer(String host, Integer port, String service) {
    it.gov.pagopa.apiconfig.starter.entity.FtpServers ftpServerE =
        getFtpServerIfExists(host, port, service);
    ftpServersRepository.delete(ftpServerE);
  }

  public PaymentTypes getPaymentTypes() {
    List<it.gov.pagopa.apiconfig.starter.entity.TipiVersamento> paymentTypeList =
        tipiVersamentoRepository.findAll();
    return it.gov.pagopa.apiconfig.core.model.configuration.PaymentTypes.builder()
        .paymentTypeList(getPaymentTypes(paymentTypeList))
        .build();
  }

  public PaymentType getPaymentType(@NotNull String paymentTypeCode) {
    TipiVersamento tipiVersamento = getTipiVersamentoIfExists(paymentTypeCode);
    return modelMapper.map(tipiVersamento, PaymentType.class);
  }

  public PaymentType createPaymentType(PaymentType paymentType) {
    Optional<TipiVersamento> pt =
        tipiVersamentoRepository.findByTipoVersamento(paymentType.getPaymentTypeCode());
    if (pt.isPresent()) {
      throw new AppException(AppError.PAYMENT_TYPE_CONFLICT, paymentType.getPaymentTypeCode());
    }

    TipiVersamento tipiVersamento = modelMapper.map(paymentType, TipiVersamento.class);
    tipiVersamentoRepository.save(tipiVersamento);

    // save on cosmos
    syncPaymentTypesHistory();

    return modelMapper.map(tipiVersamento, PaymentType.class);
  }

  public PaymentType updatePaymentType(String paymentTypeCode, PaymentTypeBase paymentType) {
    TipiVersamento tipiVersamento = getTipiVersamentoIfExists(paymentTypeCode);

    tipiVersamento.setDescrizione(paymentType.getDescription());

    tipiVersamentoRepository.save(tipiVersamento);

    // save on cosmos
    syncPaymentTypesHistory();

    return modelMapper.map(tipiVersamento, PaymentType.class);
  }

  public void deletePaymentType(String paymentTypeCode) {
    TipiVersamento tipiVersamento = getTipiVersamentoIfExists(paymentTypeCode);

    boolean removeFromMarketplace = false;

    try {
      // check if payment type is used to create bundles (AFM Marketplace)
      AfmMarketplacePaymentType response =
          afmMarketplaceClient.getPaymentType(afmMarketplaceSubscriptionKey, getRequestId(), paymentTypeCode);
      if (Boolean.TRUE.equals(response.getUsed())) {
        throw new AppException(AppError.PAYMENT_TYPE_NON_DELETABLE);
      } else {
        removeFromMarketplace = true;
      }
    } catch (FeignException e) {
      if (!(e instanceof FeignException.NotFound)) {
        throw new AppException(AppError.PAYMENT_TYPE_NO_AFM_MARKETPLACE);
      }
    }

    tipiVersamentoRepository.delete(tipiVersamento);
    if (removeFromMarketplace) {
      syncPaymentTypesHistory();
    }
  }

  private String getRequestId() {
    String header = httpServletRequest.getHeader(HEADER_REQUEST_ID);
    header = header != null ? header : UUID.randomUUID().toString();
    log.info("RequestId to call Utils: {}", header);
    return header;
  }

  public void syncPaymentTypesHistory() {
    List<PaymentTypesCosmos> paymentTypes =
        tipiVersamentoRepository.findAll().stream()
            .map(
                tipiVersamento ->
                    PaymentTypesCosmos.builder()
                        .id(CommonUtil.deNull(tipiVersamento.getId()))
                        .name(tipiVersamento.getTipoVersamento())
                        .description(tipiVersamento.getDescrizione())
                        .build())
            .collect(Collectors.toList());

    try {
      afmMarketplaceClient.syncPaymentTypes(afmMarketplaceSubscriptionKey, getRequestId(), paymentTypes);
    } catch (FeignException.BadRequest e) {
      throw new AppException(AppError.PAYMENT_TYPE_AFM_MARKETPLACE_ERROR, e.getMessage());
    } catch (FeignException e) {
      throw new AppException(AppError.PAYMENT_TYPE_BAD_REQUEST);
    }
  }

  /**
   * Maps ConfigurationKeys objects stored in the DB in a List of ConfigurationKey
   *
   * @param configurationKeysList list of configuration key returned from the database
   * @return a list of {@link it.gov.pagopa.apiconfig.core.model.configuration.ConfigurationKey}.
   */
  private List<ConfigurationKey> getConfigurationKeys(
      List<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys> configurationKeysList) {
    return configurationKeysList.stream()
        .map(elem -> modelMapper.map(elem, ConfigurationKey.class))
        .collect(Collectors.toList());
  }

  /**
   * @param category configuration category
   * @param key configuration key
   * @return return the configuration key record from DB if exists
   * @throws AppException if not found
   */
  private it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys getConfigurationKeyIfExists(
      String category, String key) throws AppException {
    Optional<it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys> result =
        configurationKeysRepository.findByConfigCategoryAndConfigKey(category, key);
    if (result.isEmpty()) {
      throw new AppException(AppError.CONFIGURATION_KEY_NOT_FOUND, category, key);
    }
    return result.get();
  }

  /**
   * Maps WfespPluginConf objects stored in the DB in a List of WfespPluginConf
   *
   * @param wfespPluginConfList list of Wfesp Plugin configuration returned from the database
   * @return a list of {@link it.gov.pagopa.apiconfig.core.model.configuration.WfespPluginConf}.
   */
  private List<WfespPluginConf> getWfespPluginConfList(
      List<it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf> wfespPluginConfList) {
    return wfespPluginConfList.stream()
        .map(elem -> modelMapper.map(elem, WfespPluginConf.class))
        .collect(Collectors.toList());
  }

  /**
   * @param idServPlugin idServPlugin
   * @return return the configuration wfesp plugin record from DB if exists
   * @throws AppException if not found
   */
  private it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf getWfespPluginConfigurationIfExists(
      String idServPlugin) throws AppException {
    Optional<it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf> result =
        wfespPluginConfRepository.findByIdServPlugin(idServPlugin);
    if (result.isEmpty()) {
      throw new AppException(AppError.CONFIGURATION_WFESP_PLUGIN_NOT_FOUND, idServPlugin);
    }
    return result.get();
  }

  /**
   * Maps Pdds objects stored in the DB in a List of Pdd
   *
   * @param pddList list of pdd returned from the database
   * @return a list of {@link it.gov.pagopa.apiconfig.core.model.configuration.Pdds}.
   */
  private List<Pdd> getPdds(List<it.gov.pagopa.apiconfig.starter.entity.Pdd> pddList) {
    return pddList.stream()
        .map(elem -> modelMapper.map(elem, Pdd.class))
        .collect(Collectors.toList());
  }

  /**
   * @param idPdd pdd identifier
   * @return return the configuration key record from DB if exists
   * @throws AppException if not found
   */
  private it.gov.pagopa.apiconfig.starter.entity.Pdd getPddIfExists(String idPdd) throws AppException {
    Optional<it.gov.pagopa.apiconfig.starter.entity.Pdd> result = pddRepository.findByIdPdd(idPdd);
    if (result.isEmpty()) {
      throw new AppException(AppError.PDD_NOT_FOUND, idPdd);
    }
    return result.get();
  }

  /**
   * Maps FtpServers objects stored in the DB in a List of FtpServer
   *
   * @param ftpServerList list of configuration key returned from the database
   * @return a list of {@link it.gov.pagopa.apiconfig.core.model.configuration.FtpServer}.
   */
  private List<FtpServer> getFtpServers(
      List<it.gov.pagopa.apiconfig.starter.entity.FtpServers> ftpServerList) {
    return ftpServerList.stream()
        .map(elem -> modelMapper.map(elem, FtpServer.class))
        .collect(Collectors.toList());
  }

  /**
   * @param host ftp server host
   * @param port ftp server host
   * @param service ftp server service
   * @return return the configuration key record from DB if exists
   * @throws AppException if not found
   */
  private it.gov.pagopa.apiconfig.starter.entity.FtpServers getFtpServerIfExists(
      String host, Integer port, String service) throws AppException {
    Optional<it.gov.pagopa.apiconfig.starter.entity.FtpServers> result =
        ftpServersRepository.findByHostAndPortAndService(host, port, service);
    if (result.isEmpty()) {
      throw new AppException(AppError.FTP_SERVER_NOT_FOUND, host, port, service);
    }
    return result.get();
  }

  /**
   * Maps TipiVersamento objects stored in the DB in a List of PaymentType
   *
   * @param paymentTypes list of tipi versamento returned from the database
   * @return a list of {@link it.gov.pagopa.apiconfig.core.model.configuration.FtpServer}.
   */
  private List<PaymentType> getPaymentTypes(
      List<it.gov.pagopa.apiconfig.starter.entity.TipiVersamento> paymentTypes) {
    return paymentTypes.stream()
        .map(elem -> modelMapper.map(elem, PaymentType.class))
        .collect(Collectors.toList());
  }

  /**
   * @param paymentTypeCode payment type code
   * @return return the configuration key record from DB if exists
   * @throws AppException if not found
   */
  private TipiVersamento getTipiVersamentoIfExists(String paymentTypeCode) throws AppException {
    Optional<TipiVersamento> result =
        tipiVersamentoRepository.findByTipoVersamento(paymentTypeCode);
    if (result.isEmpty()) {
      throw new AppException(AppError.PAYMENT_TYPE_NOT_FOUND, paymentTypeCode);
    }
    return result.get();
  }
}
