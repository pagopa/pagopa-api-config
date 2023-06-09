package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.exception.AppException;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributes;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanV2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
public class IbanServiceTest {

  @MockBean private PaRepository paRepository;

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private IbanAttributeRepository ibanAttributeRepository;

  @MockBean private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @MockBean private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired private IbanService ibanService;

  @Test
  void createIban_newIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanV2 iban = getMockIbanV2();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).thenReturn(null); // no interest in returned object
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_existingIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    String otherOwnerOrganizationFiscalCode = "anotherCI";
    IbanV2 iban = getMockIbanV2();
    Iban mockIban = getMockIban(iban, otherOwnerOrganizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).thenReturn(null); // no interest in returned object
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(otherOwnerOrganizationFiscalCode, result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_noLabelAssociated_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanV2 iban = getMockIbanV2();
    iban.setLabels(null);
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).thenThrow(IllegalArgumentException.class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() == 0);
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanV2 iban = getMockIbanV2();
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> ibanService.createIban(organizationFiscalCode, iban));;
  }

  @Test
  void createIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanV2 iban = getMockIbanV2();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void createIban_invalidLabel_422() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanV2 iban = getMockIbanV2();
    iban.getLabels().get(0).setName("FAKELABEL");
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
  }

  private Iban getMockIban(IbanV2 iban, String organizationFiscalCode) {
    return Iban.builder()
        .objId(100L)
        .iban(iban.getIbanValue())
        .fiscalCode(organizationFiscalCode)
        .description(iban.getDescription())
        .build();
  }

  private IcaBinaryFile getEmptyMockIcaBinaryFile() {
    return IcaBinaryFile.builder()
        .objId(100L)
        .fileContent(null)
        .fileHash(null)
        .fileSize(0L)
        .build();
  }

  private IbanMaster getMockIbanMaster(Pa creditorInstitution, IbanV2 iban, Iban ibanToBeCreated, IcaBinaryFile icaBinaryFile) {
    return IbanMaster.builder()
        .objId(100L)
        .fkPa(creditorInstitution.getObjId())
        .fkIban(ibanToBeCreated.getObjId())
        .fkIcaBinaryFile(icaBinaryFile.getObjId())
        .ibanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED)
        .insertedDate(CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)))
        .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
        .build();
  }
}
