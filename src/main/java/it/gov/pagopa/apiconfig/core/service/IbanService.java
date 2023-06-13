package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanV2;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansV2;
import it.gov.pagopa.apiconfig.core.util.CommonUtil;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster.IbanStatus;
import it.gov.pagopa.apiconfig.starter.entity.IcaBinaryFile;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IcaBinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional
public class IbanService {

  @Autowired private PaRepository paRepository;

  @Autowired private IbanRepository ibanRepository;

  @Autowired private IbanMasterRepository ibanMasterRepository;

  @Autowired private IbanAttributeRepository ibanAttributeRepository;

  @Autowired private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @Autowired private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired private ModelMapper modelMapper;

  public IbanV2 createIban(@NotBlank String organizationFiscalCode, @Valid @NotNull IbanV2 iban) {
    // retrieve the creditor institution and throw exception if not found
    Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
    // retrieve an existing iban or generate a new one if not defined
    Iban ibanToBeCreated = ibanRepository.findByIban(iban.getIbanValue()).orElseGet(() -> saveIban(iban, organizationFiscalCode));
    // check if IBAN was already associated to creditor institution. If already associated, throw an exception
    getIbanMaster(ibanToBeCreated, existingCreditorInstitution)
        .ifPresent(s -> {throw new AppException(AppError.IBAN_ALREADY_ASSOCIATED, iban.getIbanValue(), existingCreditorInstitution.getIdDominio());});
    // generate an empty ICA binary file and a relation between iban, CI and ICA file
    IcaBinaryFile icaBinaryFileToBeCreated = IcaBinaryFile.builder()
        .fileSize(0L)
        .build();
    icaBinaryFileToBeCreated = icaBinaryFileRepository.save(icaBinaryFileToBeCreated);
    IbanMaster ibanCIRelationToBeCreated = saveIbanCIRelation(existingCreditorInstitution, iban, ibanToBeCreated, icaBinaryFileToBeCreated);
    // generate the relation between iban and attributes
    List<IbanAttributeMaster> updatedIbanAttributes = saveIbanLabelRelation(iban, ibanCIRelationToBeCreated);
    // return final object
    return convertEntitiesToModel(existingCreditorInstitution, ibanToBeCreated, updatedIbanAttributes, ibanCIRelationToBeCreated);
  }

  public IbanV2 updateIban(@NotBlank String organizationFiscalCode, @NotBlank String ibanCode, @Valid @NotNull IbanV2 iban) {
    if (!ibanCode.equals(iban.getIbanValue())) {
      throw new AppException(HttpStatus.BAD_REQUEST, "IBAN codes not matching", "The IBAN code in the body request does not match with the IBAN code passed as path parameter.");
    }
    // retrieve the creditor institution and throw exception if not found
    Pa existingCreditorInstitution = getCreditorInstitutionIfExists(organizationFiscalCode);
    // retrieve the iban and throw exception if not found. If creditor institution is the owner, it can update the IBAN object
    Iban existingIban = ibanRepository.findByIban(ibanCode).orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND, organizationFiscalCode));
    if (organizationFiscalCode.equals(existingIban.getFiscalCode())) {
      existingIban = saveIban(iban, existingIban);
    }
    // check if IBAN was already associated to creditor institution. If not associated, throw an exception
    IbanMaster existingIbanMaster = getIbanMaster(existingIban, existingCreditorInstitution).orElseThrow(() -> new AppException(AppError.IBAN_NOT_ASSOCIATED, iban.getIbanValue(), organizationFiscalCode));
    // generate a relation between iban, CI and ICA file (this one already existing)
    IbanMaster ibanCIRelationToBeUpdated = saveIbanCIRelation(existingIbanMaster, existingCreditorInstitution, iban, existingIban, existingIbanMaster.getIcaBinaryFile());
    // remove all labels and save them again
    ibanAttributeMasterRepository.deleteAll(existingIbanMaster.getIbanAttributesMasters());
    ibanAttributeMasterRepository.flush();
    List<IbanAttributeMaster> updatedIbanAttributes = saveIbanLabelRelation(iban, ibanCIRelationToBeUpdated);
    // return final object
    return convertEntitiesToModel(existingCreditorInstitution, existingIban, updatedIbanAttributes, ibanCIRelationToBeUpdated);
  }

  public IbansV2 getCreditorInstitutionsIbansByLabel(@NotNull String organizationFiscalCode, String label) {
    List<IbanV2> ibanV2List = new ArrayList<>();
    Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
    Pa pa = creditorInstitutionOpt.orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));

    List<IbanMaster> ibanMasters = ibanMasterRepository.findByFkPa(pa.getObjId());
    ibanMasters.stream()
        .forEach(ibanMaster -> {
          Optional<Iban> ibanOpt = ibanRepository.findById(ibanMaster.getFkIban());
          Iban iban = ibanOpt.orElseThrow(() -> new AppException(AppError.IBAN_NOT_FOUND));
          Optional<Pa> ciOwnerOpt = paRepository.findByIdDominio(iban.getFiscalCode());
          Pa ciOwner = ciOwnerOpt.orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, iban.getFiscalCode()));

          if (label == null || label.isEmpty()) {
            IbanV2 ibanV2 = convertEntitiesToModel(ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
            ibanV2List.add(ibanV2);
          } else {
            boolean labelMatch = ibanMaster.getIbanAttributesMasters().stream()
                .map(ibanAttributeMaster -> ibanAttributeMaster.getIbanAttribute().getAttributeName())
                .anyMatch(name -> name.equalsIgnoreCase(label));

            if (labelMatch) {
              IbanV2 ibanV2 = convertEntitiesToModel(ciOwner, iban, ibanMaster.getIbanAttributesMasters(), ibanMaster);
              ibanV2List.add(ibanV2);
            }
          }
        });

    return IbansV2.builder().ibanV2List(ibanV2List).build();
  }

  private Pa getCreditorInstitutionIfExists(String organizationFiscalCode) {
    // retrieve the creditor institution and throw exception if not found
    Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
    return creditorInstitutionOpt.orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));
  }

  private Optional<IbanMaster> getIbanMaster(Iban iban, Pa creditorInstitution) {
    return ibanMasterRepository.findByFkIbanAndFkPa(iban.getObjId(), creditorInstitution.getObjId())
        .stream()
        .findFirst();
  }

  private Iban saveIban(IbanV2 iban, String organizationFiscalCode) {
    Iban ibanToBeCreated = Iban.builder()
        .iban(iban.getIbanValue())
        .fiscalCode(organizationFiscalCode)
        .description(iban.getDescription())
        .build();
    return saveIban(iban, ibanToBeCreated);
  }

  private Iban saveIban(IbanV2 iban, Iban existingIban) {
    existingIban.setDescription(iban.getDescription());
    return ibanRepository.save(existingIban);
  }

  private IbanMaster saveIbanCIRelation(Pa creditorInstitution, IbanV2 iban, Iban ibanToBeCreated, IcaBinaryFile icaBinaryFileToBeCreated) {
    return saveIbanCIRelation(new IbanMaster(), creditorInstitution, iban, ibanToBeCreated, icaBinaryFileToBeCreated);
  }

  private IbanMaster saveIbanCIRelation(IbanMaster ibanMaster, Pa creditorInstitution, IbanV2 iban, Iban ibanToBeCreated, IcaBinaryFile icaBinaryFileToBeCreated) {
    ibanMaster.setFkPa(creditorInstitution.getObjId());
    ibanMaster.setFkIban(ibanToBeCreated.getObjId());
    ibanMaster.setFkIcaBinaryFile(icaBinaryFileToBeCreated.getObjId());
    ibanMaster.setIbanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED);
    ibanMaster.setInsertedDate(ibanMaster.getInsertedDate() != null ? ibanMaster.getInsertedDate() : CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)));
    ibanMaster.setValidityDate(CommonUtil.toTimestamp(iban.getValidityDate()));
    ibanMaster.setPa(creditorInstitution); // setting CI object reference
    ibanMaster.setIban(ibanToBeCreated); // setting IBAN object reference
    ibanMaster.setIcaBinaryFile(icaBinaryFileToBeCreated); // setting ICA binary file object reference
    return ibanMasterRepository.save(ibanMaster);
  }


    private List<IbanAttributeMaster> saveIbanLabelRelation(IbanV2 iban, IbanMaster ibanCIRelation) {
    // validating and inserting the labels
    Map<String, IbanAttribute> validLabels = ibanAttributeRepository.findAll()
        .stream()
        .collect(Collectors.toMap(IbanAttribute::getAttributeName, obj -> obj));

    List<IbanAttributeMaster> labels = new LinkedList<>();
    // Analyzing the label from IBAN object passed as input (analyze an empty list if passed as null)
    for (IbanLabel label : Optional.ofNullable(iban.getLabels()).orElse(List.of())) {
      /*
        Get the IBAN attribute using the label from list as search key from the valid label map:
         - If found, generate the entity to be saved.
         - If not found (null result), throw an exception and stop computation.
       */
      IbanAttribute ibanAttribute = Optional.ofNullable(validLabels.get(label.getName()))
          .orElseThrow(() -> new AppException(AppError.IBAN_LABEL_NOT_VALID, label.getName()));
      IbanAttributeMaster ibanAttributesMasterToBeCreated = IbanAttributeMaster.builder()
          .fkIbanMaster(ibanCIRelation.getObjId())
          .fkAttribute(ibanAttribute.getObjId())
          .ibanMaster(ibanCIRelation)
          .ibanAttribute(ibanAttribute)
          .build();
      labels.add(ibanAttributeMasterRepository.save(ibanAttributesMasterToBeCreated));
    }
    return labels;
  }

  private IbanV2 convertEntitiesToModel(Pa creditorInstitution, Iban iban, List<IbanAttributeMaster> ibanAttributes, IbanMaster ibanCIRelation) {
    return IbanV2.builder()
        .companyName(creditorInstitution.getRagioneSociale())
        .ibanValue(iban.getIban())
        .description(iban.getDescription())
        .labels(
            Optional.of(
                ibanAttributes.stream()
                    .map(obj -> modelMapper.map(obj, IbanLabel.class))
                    .collect(Collectors.toList())
            ).orElse(List.of()))
        .ciOwnerFiscalCode(iban.getFiscalCode())
        .isActive(IbanStatus.ENABLED.equals(ibanCIRelation.getIbanStatus()))
        .validityDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getValidityDate()))
        .publicationDate(CommonUtil.toOffsetDateTime(ibanCIRelation.getInsertedDate()))
        .build();
  }
}
