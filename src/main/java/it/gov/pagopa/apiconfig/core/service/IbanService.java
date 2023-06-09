package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanV2;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    Optional<Pa> creditorInstitutionOpt = paRepository.findByIdDominio(organizationFiscalCode);
    Pa existingCreditorInstitution = creditorInstitutionOpt.orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, organizationFiscalCode));
    // retrieve an existing iban or generate a new one if not defined
    Iban ibanToBeCreated = ibanRepository.findByIban(iban.getIbanValue()).orElse(null);
    if (ibanToBeCreated == null) {
      ibanToBeCreated = Iban.builder()
          .iban(iban.getIbanValue())
          .fiscalCode(organizationFiscalCode)
          .description(iban.getDescription())
          .build();
      ibanToBeCreated = ibanRepository.save(ibanToBeCreated);
    }
    // generate an empty ICA binary file and a relation between iban, CI and ICA file
    IcaBinaryFile icaBinaryFileToBeCreated = IcaBinaryFile.builder()
        .fileSize(0L)
        .build();
    icaBinaryFileToBeCreated = icaBinaryFileRepository.save(icaBinaryFileToBeCreated);
    IbanMaster ibanCIRelationToBeCreated = IbanMaster.builder()
        .fkPa(existingCreditorInstitution.getObjId())
        .fkIban(ibanToBeCreated.getObjId())
        .fkIcaBinaryFile(icaBinaryFileToBeCreated.getObjId())
        .ibanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED)
        .insertedDate(CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)))
        .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
        .build();
    ibanCIRelationToBeCreated = ibanMasterRepository.save(ibanCIRelationToBeCreated);
    // validating and inserting the labels
    Map<String, IbanAttribute> validLabels = ibanAttributeRepository.findAll()
        .stream()
        .collect(Collectors.toMap(IbanAttribute::getAttributeName, obj -> obj));

    // Analyzing the label from IBAN object passed as input (analyze an empty list if passed as null)
    for (IbanLabel label : Optional.ofNullable(iban.getLabels()).orElse(List.of())) {
      /*
        Get the IBAN attribute using the label from list as search key from the valid label map:
         - If found, generate the entity to be saved.
         - If not found (null result), throw an exception and stop computation.
       */
      IbanAttribute ibanAttribute = Optional.ofNullable(validLabels.get(label.getName()))
          .orElseThrow(() -> new AppException(AppError.IBAN_LABEL_NOT_VALID, label));
      IbanAttributeMaster ibanAttributesMasterToBeCreated = IbanAttributeMaster.builder()
          .fkIbanMaster(ibanCIRelationToBeCreated.getObjId())
          .fkAttributeMaster(ibanAttribute.getObjId())
          .build();
      ibanAttributeMasterRepository.save(ibanAttributesMasterToBeCreated);
    }

    return IbanV2.builder()
        .companyName(existingCreditorInstitution.getRagioneSociale())
        .ibanValue(ibanToBeCreated.getIban())
        .description(ibanToBeCreated.getDescription())
        .labels(Optional.ofNullable(iban.getLabels()).orElse(List.of()))
        .ciOwnerFiscalCode(ibanToBeCreated.getFiscalCode())
        .isActive(IbanStatus.ENABLED.equals(ibanCIRelationToBeCreated.getIbanStatus()))
        .validityDate(CommonUtil.toOffsetDateTime(ibanCIRelationToBeCreated.getValidityDate()))
        .publicationDate(CommonUtil.toOffsetDateTime(ibanCIRelationToBeCreated.getInsertedDate()))
        .build();
  }

}
