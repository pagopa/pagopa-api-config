package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding.CodeTypeEnum;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster.IbanStatus;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
public class IbanService {

  @Value("${iban.abi.poste}")
  private String postalIbanAbi;

  @Value("${iban.labels.cup}")
  private String cupLabel;

  @Value("${iban.labels.aca}")
  private String acaLabel;

  @Autowired private PaRepository paRepository;

  @Autowired private IbanRepository ibanRepository;

  @Autowired private IbanMasterRepository ibanMasterRepository;

  @Autowired private IbanAttributeRepository ibanAttributeRepository;

  @Autowired private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @Autowired private EncodingsService encodingsService;

  @Autowired private ModelMapper modelMapper;

  @Autowired private AzureStorageInteraction azureStorageInteraction;

  public IbanEnhanced createIban(
      @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid") @NotBlank
          String organizationFiscalCode,
      @Valid @NotNull IbanEnhanced iban) {
    // retrieve the creditor institution and throw exception if not found
    Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
    // Update Ica Table
    azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());
    // retrieve an existing iban or generate a new one if not defined
    Iban ibanToBeCreated =
        ibanRepository
            .findByIban(iban.getIbanValue())
            .orElseGet(() -> saveIban(iban, organizationFiscalCode));
    // check if IBAN is a postal IBAN and if it is already related to an existing Creditor
    // Institution
    if (isPostalIban(iban.getIbanValue())
        && !ibanMasterRepository.findByFkIban(ibanToBeCreated.getObjId()).isEmpty())
      throw new AppException(
          AppError.POSTAL_IBAN_ALREADY_ASSOCIATED,
          iban.getIbanValue(),
          existingCreditorInstitution.getIdDominio());
    // check if IBAN was already associated to creditor institution. If already associated, throw an
    // exception
    getIbanMaster(ibanToBeCreated, existingCreditorInstitution)
        .ifPresent(
            s -> {
              throw new AppException(
                  AppError.IBAN_ALREADY_ASSOCIATED,
                  iban.getIbanValue(),
                  existingCreditorInstitution.getIdDominio());
            });
    IbanMaster ibanCIRelationToBeCreated =
        saveIbanCIRelation(existingCreditorInstitution, iban, ibanToBeCreated);
    // generate the relation between iban and attributes
    List<IbanAttributeMaster> updatedIbanAttributes =
        saveIbanLabelRelation(iban, ibanCIRelationToBeCreated);
    // create encoding for postal iban
    if (isPostalIban(iban.getIbanValue())) {
      String ibanValue = iban.getIbanValue();
      Encoding encoding =
          Encoding.builder()
              .codeType(CodeTypeEnum.BARCODE_128_AIM)
              .encodingCode(
                  ibanValue.substring(
                      ibanValue.length()
                          - 12)) // for BARCODE-128-AIM encoding code equals to last 12 characters
              // of iban value
              .build();

      encodingsService.createCreditorInstitutionEncoding(
          existingCreditorInstitution.getIdDominio(), encoding);
    }

    // return final object
    return convertEntitiesToModel(
        existingCreditorInstitution,
        ibanToBeCreated,
        updatedIbanAttributes,
        ibanCIRelationToBeCreated);
  }

  public IbanEnhanced updateIban(
      @NotBlank @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
          String organizationFiscalCode,
      @NotBlank
          @Pattern(regexp = "[a-zA-Z]{2}\\d{2}[a-zA-Z0-9]{1,30}", message = "IBAN code not valid")
          String ibanCode,
      @Valid @NotNull IbanEnhanced iban) {
    if (!ibanCode.equals(iban.getIbanValue())) {
      throw new AppException(
          HttpStatus.BAD_REQUEST,
          "IBAN codes not matching",
          "The IBAN code in the body request does not match with the IBAN code passed as path"
              + " parameter.");
    }
    // retrieve the creditor institution and throw exception if not found
    Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
    // Update Ica Table
    azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());
    // retrieve the iban and throw exception if not found. If creditor institution is the owner, it
    // can update the IBAN object
    Iban existingIban =
        ibanRepository
            .findByIban(ibanCode)
            .orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, organizationFiscalCode));
    if (organizationFiscalCode.equals(existingIban.getFiscalCode())) {
      existingIban = saveIban(iban, existingIban);
    }
    // check if IBAN was already associated to creditor institution. If not associated, throw an
    // exception
    IbanMaster existingIbanMaster =
        getIbanMaster(existingIban, existingCreditorInstitution)
            .orElseThrow(
                () ->
                    new AppException(
                        AppError.IBAN_NOT_ASSOCIATED, iban.getIbanValue(), organizationFiscalCode));
    // generate a relation between iban and CI
    IbanMaster ibanCIRelationToBeUpdated =
        saveIbanCIRelation(existingIbanMaster, existingCreditorInstitution, iban, existingIban);
    // remove all labels and save them again
    ibanAttributeMasterRepository.deleteAll(existingIbanMaster.getIbanAttributesMasters());
    ibanAttributeMasterRepository.flush();
    List<IbanAttributeMaster> updatedIbanAttributes =
        saveIbanLabelRelation(iban, ibanCIRelationToBeUpdated);
    // return final object
    return convertEntitiesToModel(
        existingCreditorInstitution,
        existingIban,
        updatedIbanAttributes,
        ibanCIRelationToBeUpdated);
  }

  public IbansEnhanced getCreditorInstitutionsIbansByLabel(
      @NotNull @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
          String organizationFiscalCode,
      String label) {
    List<IbanEnhanced> ibanEnhancedList = new ArrayList<>();
    Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
    Pa pa =
        creditorInstitutionOpt.orElseThrow(
            () ->
                new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));

    List<IbanMaster> ibanMasters = ibanMasterRepository.findByFkPa(pa.getObjId());
    ibanMasters.forEach(
        ibanMaster -> {
          Optional<Iban> ibanOpt = ibanRepository.findById(ibanMaster.getFkIban());
          Iban iban = ibanOpt.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND));
          Optional<Pa> ciOwnerOpt = paRepository.findByIdDominio(iban.getFiscalCode());
          Pa ciOwner =
              ciOwnerOpt.orElseThrow(
                  () ->
                      new AppException(
                          AppError.CREDITOR_INSTITUTION_NOT_FOUND, iban.getFiscalCode()));

          if (label == null || label.isEmpty()) {
            IbanEnhanced ibanEnhanced =
                convertEntitiesToModel(
                    ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
            ibanEnhancedList.add(ibanEnhanced);
          } else {
            boolean labelMatch =
                ibanMaster.getIbanAttributesMasters().stream()
                    .map(
                        ibanAttributeMaster ->
                            ibanAttributeMaster.getIbanAttribute().getAttributeName())
                    .anyMatch(name -> name.equalsIgnoreCase(label));

            if (labelMatch) {
              IbanEnhanced ibanEnhanced =
                  convertEntitiesToModel(
                      ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
              ibanEnhancedList.add(ibanEnhanced);
            }
          }
        });

    if(ibanEnhancedList.isEmpty() && (label.equals(acaLabel) || label.equals(cupLabel))) {
        IbanMaster lastPublishedIban = getLastPublishedIban(pa);
        if(lastPublishedIban != null) {
            ibanEnhancedList.add(convertEntitiesToModel(pa, lastPublishedIban.getIban(), lastPublishedIban.getIbanAttributesMasters(), lastPublishedIban));
        }
    }

    return IbansEnhanced.builder().ibanEnhancedList(ibanEnhancedList).build();
  }

  public String deleteIban(
      @NotBlank @Pattern(regexp = "\\d{11}", message = "CI fiscal code not valid")
          String organizationFiscalCode,
      @NotNull
          @Pattern(regexp = "[a-zA-Z]{2}\\d{2}[a-zA-Z0-9]{1,30}", message = "IBAN code not valid")
          String ibanValue) {
    // Get iban entity to be deleted
    Iban ibanToBeDeleted = getIbanIfExists(ibanValue);

    // Get pa entity
    Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);

    // Update Ica Table
    azureStorageInteraction.updateECIcaTable(existingCreditorInstitution.getIdDominio());

    // Get all ibanMaster relations
    List<IbanMaster> ibanMastersToBeDeleted =
        ibanMasterRepository.findByFkIbanAndFkPa(
            ibanToBeDeleted.getObjId(), existingCreditorInstitution.getObjId());

    // Get all ibanAttributesMaster relations
    List<IbanAttributeMaster> ibanAttributeMastersToBeDeleted =
        ibanAttributeMasterRepository.findByFkIbanMasterIn(
            ibanMastersToBeDeleted.stream().map(IbanMaster::getObjId).collect(Collectors.toList()));

    // Delete related encoding if postal iban
    if (isPostalIban(ibanToBeDeleted.getIban())) deleteEncoding(organizationFiscalCode, ibanValue);

    // Delete all relations listed before
    ibanAttributeMasterRepository.deleteAll(ibanAttributeMastersToBeDeleted);
    ibanMasterRepository.deleteAll(ibanMastersToBeDeleted);
    if (ibanMasterRepository.findByFkIban(ibanToBeDeleted.getObjId()).isEmpty()) {
      ibanRepository.delete(ibanToBeDeleted);
    }

    return String.format(
        "\"The Iban %s for the creditor institution %s has been deleted\"",
        ibanValue, organizationFiscalCode);
  }

  public boolean isPostalIban(String ibanValue) {
    String abiCode = ibanValue.substring(5, 10);
    return abiCode.equals(postalIbanAbi);
  }

  private IbanMaster getLastPublishedIban(Pa pa) {
      List<IbanMaster> activeIbans = pa.getIbanMasters().stream()
              .filter(ibanPa -> ibanPa.getValidityDate().after(Timestamp.valueOf(LocalDateTime.now())))
              .collect(Collectors.toList());
      return activeIbans.stream().max(Comparator.comparing(IbanMaster::getInsertedDate)).orElse(null);
  }

  private Pa getCreditorInstitutionIfExists(String organizationFiscalCode) {
    // retrieve the creditor institution and throw exception if not found
    Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
    return creditorInstitutionOpt.orElseThrow(
        () -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));
  }

  private Optional<IbanMaster> getIbanMaster(Iban iban, Pa creditorInstitution) {
    return ibanMasterRepository
        .findByFkIbanAndFkPa(iban.getObjId(), creditorInstitution.getObjId())
        .stream()
        .findFirst();
  }

  private Iban saveIban(IbanEnhanced iban, String organizationFiscalCode) {
    Iban ibanToBeCreated =
        Iban.builder()
            .iban(iban.getIbanValue())
            .fiscalCode(organizationFiscalCode)
            .dueDate(CommonUtil.toTimestamp(iban.getDueDate()))
            .build();
    return saveIban(iban, ibanToBeCreated);
  }

  private Iban saveIban(IbanEnhanced iban, Iban existingIban) {
    existingIban.setDueDate(CommonUtil.toTimestamp(iban.getDueDate()));
    return ibanRepository.save(existingIban);
  }

  private IbanMaster saveIbanCIRelation(
      Pa creditorInstitution, IbanEnhanced iban, Iban ibanToBeCreated) {
    return saveIbanCIRelation(new IbanMaster(), creditorInstitution, iban, ibanToBeCreated);
  }

  private IbanMaster saveIbanCIRelation(
      IbanMaster ibanMaster, Pa creditorInstitution, IbanEnhanced iban, Iban ibanToBeCreated) {
    ibanMaster.setFkPa(creditorInstitution.getObjId());
    ibanMaster.setFkIban(ibanToBeCreated.getObjId());
    ibanMaster.setIbanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED);
    ibanMaster.setInsertedDate(
        ibanMaster.getInsertedDate() != null
            ? ibanMaster.getInsertedDate()
            : CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)));
    ibanMaster.setValidityDate(CommonUtil.toTimestamp(iban.getValidityDate()));
    ibanMaster.setDescription(iban.getDescription());
    ibanMaster.setPa(creditorInstitution); // setting CI object reference
    ibanMaster.setIban(ibanToBeCreated); // setting IBAN object reference
    return ibanMasterRepository.save(ibanMaster);
  }

  private List<IbanAttributeMaster> saveIbanLabelRelation(
      IbanEnhanced iban, IbanMaster ibanCIRelation) {
    // validating and inserting the labels
    Map<String, IbanAttribute> validLabels =
        ibanAttributeRepository.findAll().stream()
            .collect(Collectors.toMap(IbanAttribute::getAttributeName, obj -> obj));

    List<IbanAttributeMaster> labels = new LinkedList<>();
    // Analyzing the label from IBAN object passed as input (analyze an empty list if passed as
    // null)
    for (IbanLabel label : Optional.ofNullable(iban.getLabels()).orElse(List.of())) {
      /*
       Get the IBAN attribute using the label from list as search key from the valid label map:
        - If found, generate the entity to be saved.
        - If not found (null result), throw an exception and stop computation.
      */
      IbanAttribute ibanAttribute =
          Optional.ofNullable(validLabels.get(label.getName()))
              .orElseThrow(() -> new AppException(AppError.IBAN_LABEL_NOT_VALID, label.getName()));
      IbanAttributeMaster ibanAttributesMasterToBeCreated =
          IbanAttributeMaster.builder()
              .fkIbanMaster(ibanCIRelation.getObjId())
              .fkAttribute(ibanAttribute.getObjId())
              .ibanMaster(ibanCIRelation)
              .ibanAttribute(ibanAttribute)
              .build();
      labels.add(ibanAttributeMasterRepository.save(ibanAttributesMasterToBeCreated));
    }
    return labels;
  }

  private void deleteEncoding(String organizationFiscalCode, String ibanValue) {
    // check the relation between encoding and CI and delete CI encoding
    @NotNull
    @Valid
    List<Encoding> encodings =
        encodingsService.getCreditorInstitutionEncodings(organizationFiscalCode).getEncodings();
    String econdingToDelete = ibanValue.substring(ibanValue.length() - 12);
    boolean existEncoding =
        encodings.stream()
            .map(Encoding::getEncodingCode)
            .anyMatch(encodingCode -> encodingCode.equals(econdingToDelete));
    if (existEncoding)
      encodingsService.deleteCreditorInstitutionEncoding(organizationFiscalCode, econdingToDelete);
  }

  private IbanEnhanced convertEntitiesToModel(
      Pa creditorInstitution,
      Iban iban,
      List<IbanAttributeMaster> ibanAttributes,
      IbanMaster ibanCIRelation) {
    return IbanEnhanced.builder()
        .companyName(creditorInstitution.getRagioneSociale())
        .ibanValue(iban.getIban())
        .description(ibanCIRelation.getDescription())
        .labels(
            Optional.of(
                    ibanAttributes.stream()
                        .map(obj -> modelMapper.map(obj, IbanLabel.class))
                        .collect(Collectors.toList()))
                .orElse(List.of()))
        .ciOwnerFiscalCode(iban.getFiscalCode())
        .isActive(IbanStatus.ENABLED.equals(ibanCIRelation.getIbanStatus()))
        .validityDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getValidityDate()))
        .publicationDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getInsertedDate()))
        .dueDate(CommonUtil.toOffsetDateTime(iban.getDueDate()))
        .build();
  }

  private Iban getIbanIfExists(String ibanValue) {
    return ibanRepository
        .findByIban(ibanValue)
        .orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, ibanValue));
  }
}
