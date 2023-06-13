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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import javax.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributeMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributes;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEntity;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanMaster_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanV2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class IbanServiceTest {

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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).then(returnsFirstArg());
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).then(returnsFirstArg());
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(otherOwnerOrganizationFiscalCode, result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
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
    assertEquals(0, result.getLabels().size());
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
    ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
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
  void createIban_existingRelationWithCI_409() {
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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).then(returnsFirstArg());
    // executing logic and check assertions
    IbanV2 result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
    // trying to regenerating the same IBAN relation
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of(mockIbanMaster));
    AppException ex = assertThrows(AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
  }


  @Test
  void updateIban_newIban_200() {
    // retrieving mock object
    IbanV2 iban = getMockIbanV2();
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of(mockIbanMaster));
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(updatedMockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    doNothing().when(ibanAttributeMasterRepository).deleteAll(any());
    doNothing().when(ibanAttributeMasterRepository).flush();
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).then(returnsFirstArg());
    // executing logic and check assertions
    IbanV2 result = ibanService.updateIban(organizationFiscalCode, iban.getIbanValue(), iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(1, result.getLabels().size());
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void updateIban_noLabelAssociated_200() {
    // retrieving mock object
    IbanV2 iban = getMockIbanV2();
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of(mockIbanMaster));
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(updatedMockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    doNothing().when(ibanAttributeMasterRepository).deleteAll(any());
    doNothing().when(ibanAttributeMasterRepository).flush();
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).thenThrow(IllegalArgumentException.class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    IbanV2 result = ibanService.updateIban(organizationFiscalCode, iban.getIbanValue(), iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC), result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(0, result.getLabels().size());
    assertTrue(result.getPublicationDate().withOffsetSameLocal(ZoneOffset.UTC).isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void updateIban_genericConstraintViolation_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanV2 iban = getMockIbanV2();
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    assertThrows(ConstraintViolationException.class, () -> ibanService.updateIban(organizationFiscalCode, "IT99C0222211111000000000000", iban));
  }

  @Test
  void updateIban_ibanCodesConstraintViolation_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanV2 iban = getMockIbanV2();
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.updateIban(organizationFiscalCode, "fakeiban", iban));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  void updateIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    String ibanValue = "IT99C0222211111000000000000";
    IbanV2 iban = getMockIbanV2();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_noIbanFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    String ibanValue = "IT99C0222211111000000000000";
    IbanV2 iban = getMockIbanV2();
    Pa creditorInstitution = getMockPa();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_noIbanCIRelationFound_404() {    // retrieving mock object
    IbanV2 iban = getMockIbanV2();
    String ibanValue = "IT99C0222211111000000000000";
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_invalidLabel_422() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String ibanValue = "IT99C0222211111000000000000";
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
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class))).thenThrow(IllegalArgumentException.class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    AppException ex = assertThrows(AppException.class, () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
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
        .pa(creditorInstitution)
        .fkIban(ibanToBeCreated.getObjId())
        .iban(ibanToBeCreated)
        .fkIcaBinaryFile(icaBinaryFile.getObjId())
        .icaBinaryFile(icaBinaryFile)
        .ibanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED)
        .insertedDate(CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)))
        .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
        .build();
  }

  @Test
  void deleteIban(){
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(getMockIbanEntity()));
    when(paRepository.findByIdDominio(any())).thenReturn(Optional.of(getMockPa()));
    when(ibanMasterRepository.findByFkIbanAndFkPa(any(), any())).thenReturn(List.of(getMockIbanMaster_2()));
    when(ibanAttributeMasterRepository.findByFkIbanMasterIn(any())).thenReturn(List.of(getMockIbanAttributeMaster()));

    ibanService.deleteIban("00168480242", "IT99C0222211111000000000000");
    Mockito.verify(ibanRepository, times(1)).findByIban("IT99C0222211111000000000000");
    Mockito.verify(paRepository, times(1)).findByIdDominio("00168480242");
    Mockito.verify(ibanMasterRepository, times(1)).findByFkIbanAndFkPa(1L, 1L);
    Mockito.verify(ibanAttributeMasterRepository, times(1)).findByFkIbanMasterIn(List.of(1L));
    assertTrue(true);
  }

  @Test
  void deleteIban_NotFound(){
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    try {
      ibanService.deleteIban("1234", "IT99C0222211111000000000000");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }
}
