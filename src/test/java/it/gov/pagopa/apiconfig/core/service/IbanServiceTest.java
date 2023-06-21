package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributeMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributeMasters;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributes;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEnhanced;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEntity;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanMaster_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPostalIbanEnhanced;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
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
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = ApiConfig.class)
class IbanServiceTest {

  @MockBean private PaRepository paRepository;

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private IbanAttributeRepository ibanAttributeRepository;

  @MockBean private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @MockBean private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired private IbanService ibanService;

  // PA IBAN owner differs from PA linked in IBAN_MASTER
  @Test
  void getIbansEnhanced_200()
      throws IOException,
          JSONException,
          org.springframework.boot.configurationprocessor.json.JSONException {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban =
        getMockIbanEnhanced(
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"),
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    List<IbanMaster> mockIbanMasters =
        getMockIbanMasters(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanMasterRepository.findByFkPa(creditorInstitution.getObjId()))
        .thenReturn(mockIbanMasters);
    when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
    // executing logic and check assertions
    IbansEnhanced result =
        ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, "STANDIN");
    String actual = TestUtil.toJson(result);
    // Force date value for check
    JSONObject obj = new JSONObject(actual).getJSONArray("ibans_enhanced").getJSONObject(0);
    obj.put("publication_date", "2023-05-23T10:38:07.165Z");
    obj.put("validity_date", "2023-06-07T13:48:15.166Z");
    obj.put("due_date", "2023-06-07T13:48:15.166Z");
    actual = new JSONObject(actual).put("ibans_enhanced", new JSONArray().put(obj)).toString();
    String expected =
        TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans_enhanced.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getIbansEnhanced_DifferentPA_200()
      throws IOException,
          JSONException,
          org.springframework.boot.configurationprocessor.json.JSONException {
    // retrieving mock object
    Pa pa1 = getMockPa();
    Pa pa2 = getMockPa2();
    IbanEnhanced iban =
        getMockIbanEnhanced(
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"),
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"));
    Iban mockIban = getMockIban(iban, pa1.getIdDominio());
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    List<IbanMaster> mockIbanMasters = getMockIbanMasters(pa2, iban, mockIban, mockIcaBinaryFile);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(pa1.getIdDominio())).thenReturn(Optional.of(pa1));
    when(paRepository.findByIdDominio(pa2.getIdDominio())).thenReturn(Optional.of(pa2));
    when(ibanMasterRepository.findByFkPa(pa2.getObjId())).thenReturn(mockIbanMasters);
    when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
    // executing logic and check assertions
    IbansEnhanced result =
        ibanService.getCreditorInstitutionsIbansByLabel(pa2.getIdDominio(), "STANDIN");
    String actual = TestUtil.toJson(result);
    // Force date value for check
    JSONObject obj = new JSONObject(actual).getJSONArray("ibans_enhanced").getJSONObject(0);
    obj.put("publication_date", "2023-05-23T10:38:07.165Z");
    obj.put("validity_date", "2023-06-07T13:48:15.166Z");
    obj.put("due_date", "2023-06-07T13:48:15.166Z");
    actual = new JSONObject(actual).put("ibans_enhanced", new JSONArray().put(obj)).toString();
    String expected =
        TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans_enhanced.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getIbansEnhanced_noLabel_200()
      throws IOException,
          JSONException,
          org.springframework.boot.configurationprocessor.json.JSONException {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban =
        getMockIbanEnhanced(
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"),
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    List<IbanMaster> mockIbanMasters =
        getMockIbanMasters(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanMasterRepository.findByFkPa(creditorInstitution.getObjId()))
        .thenReturn(mockIbanMasters);
    when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
    // executing logic and check assertions
    IbansEnhanced result =
        ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, null);
    String actual = TestUtil.toJson(result);
    // Force date value for check
    JSONObject obj = new JSONObject(actual).getJSONArray("ibans_enhanced").getJSONObject(0);
    obj.put("publication_date", "2023-05-23T10:38:07.165Z");
    obj.put("validity_date", "2023-06-07T13:48:15.166Z");
    obj.put("due_date", "2023-06-07T13:48:15.166Z");
    actual = new JSONObject(actual).put("ibans_enhanced", new JSONArray().put(obj)).toString();
    String expected =
        TestUtil.readJsonFromFile("response/get_creditorinstitution_ibans_enhanced.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getIbansEnhanced_EmptyIbanList_200()
      throws JsonProcessingException,
          JSONException,
          org.springframework.boot.configurationprocessor.json.JSONException {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban =
        getMockIbanEnhanced(
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"),
            OffsetDateTime.parse("2023-06-07T13:48:15.166Z"));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    List<IbanMaster> mockIbanMasters =
        getMockIbanMasters(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanMasterRepository.findByFkPa(creditorInstitution.getObjId()))
        .thenReturn(mockIbanMasters);
    when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
    // executing logic and check assertions
    IbansEnhanced result =
        ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, "CUP");
    String actual = TestUtil.toJson(result);
    String expected =
        TestUtil.toJson(IbansEnhanced.builder().ibanEnhancedList(new ArrayList<>()).build());
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getIbansEnhanced_noCIFound_404() throws JsonProcessingException, JSONException {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, "CUP"));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void createIban_newIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    // executing logic and check assertions
    IbanEnhanced result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_existingIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    String otherOwnerOrganizationFiscalCode = "anotherCI";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Iban mockIban = getMockIban(iban, otherOwnerOrganizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    // executing logic and check assertions
    IbanEnhanced result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(otherOwnerOrganizationFiscalCode, result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_noLabelAssociated_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setLabels(null);
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .thenThrow(
            IllegalArgumentException
                .class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    IbanEnhanced result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(0, result.getLabels().size());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void createIban_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    ConstraintViolationException ex =
        assertThrows(
            ConstraintViolationException.class,
            () -> ibanService.createIban(organizationFiscalCode, iban));
  }

  @Test
  void createIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void createIban_existingRelationWithCI_409() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    // executing logic and check assertions
    IbanEnhanced result = ibanService.createIban(organizationFiscalCode, iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(result.getLabels().size() > 0);
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
    // trying to regenerating the same IBAN relation
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong()))
        .thenReturn(List.of(mockIbanMaster));
    AppException ex =
        assertThrows(
            AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
  }

  @Test
  void createPostalIban_existingRelationWithOneCI_409() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    when(ibanMasterRepository.findByFkIban(anyLong())).thenReturn(getMockIbanMasters(creditorInstitution, iban, mockIban, mockIcaBinaryFile));
    AppException ex =
        assertThrows(
            AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
  }

  @Test
  void createIban_invalidLabel_422() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.getLabels().get(0).setName("FAKELABEL");
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(icaBinaryFileRepository.save(any(IcaBinaryFile.class))).thenReturn(mockIcaBinaryFile);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class, () -> ibanService.createIban(organizationFiscalCode, iban));
    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ex.getHttpStatus());
  }

  @Test
  void updateIban_newIban_200() {
    // retrieving mock object
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    IbanMaster updatedMockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong()))
        .thenReturn(List.of(mockIbanMaster));
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(updatedMockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    doNothing().when(ibanAttributeMasterRepository).deleteAll(any());
    doNothing().when(ibanAttributeMasterRepository).flush();
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    // executing logic and check assertions
    IbanEnhanced result = ibanService.updateIban(organizationFiscalCode, iban.getIbanValue(), iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(1, result.getLabels().size());
    assertEquals(result.getLabels().get(0).getName(), iban.getLabels().get(0).getName());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void updateIban_noLabelAssociated_200() {
    // retrieving mock object
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    IbanMaster updatedMockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong()))
        .thenReturn(List.of(mockIbanMaster));
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(updatedMockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    doNothing().when(ibanAttributeMasterRepository).deleteAll(any());
    doNothing().when(ibanAttributeMasterRepository).flush();
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .thenThrow(
            IllegalArgumentException
                .class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    IbanEnhanced result = ibanService.updateIban(organizationFiscalCode, iban.getIbanValue(), iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(creditorInstitution.getRagioneSociale(), result.getCompanyName());
    assertEquals(creditorInstitution.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(0, result.getLabels().size());
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void updatePostalIbanByDifferentCI_200() { // retrieving mock object
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Pa pa1 = getMockPa();
    String fc1 = pa1.getIdDominio();
    Pa pa2 = getMockPa2();
    String fc2 = pa1.getIdDominio();
    Iban mockIban = getMockIban(iban, fc1);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    List<IbanMaster> ibanMasters = getMockIbanMasters(pa2, iban, mockIban, mockIcaBinaryFile);
    IbanMaster updatedMockIbanMaster =
        getMockIbanMaster(pa2, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(fc1))
        .thenReturn(Optional.of(pa1));
    when(paRepository.findByIdDominio(fc2))
        .thenReturn(Optional.of(pa2));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), eq(pa2.getObjId())))
        .thenReturn(ibanMasters);
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(updatedMockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    doNothing().when(ibanAttributeMasterRepository).deleteAll(any());
    doNothing().when(ibanAttributeMasterRepository).flush();
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    // executing logic and check assertions
    IbanEnhanced result = ibanService.updateIban(fc2, iban.getIbanValue(), iban);
    assertEquals(iban.isActive(), result.isActive());
    assertEquals(iban.getIbanValue(), result.getIbanValue());
    assertEquals(iban.getDescription(), result.getDescription());
    assertEquals(pa2.getRagioneSociale(), result.getCompanyName());
    assertEquals(pa1.getIdDominio(), result.getCiOwnerFiscalCode());
    assertEquals(
        iban.getDueDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getDueDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertEquals(
        iban.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC),
        result.getValidityDate().withOffsetSameLocal(ZoneOffset.UTC));
    assertTrue(
        result
            .getPublicationDate()
            .withOffsetSameLocal(ZoneOffset.UTC)
            .isBefore(OffsetDateTime.now().withOffsetSameLocal(ZoneOffset.UTC)));
  }

  @Test
  void updateIban_genericConstraintViolation_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.updateIban(organizationFiscalCode, "IT99C0222211111000000000000", iban));
  }

  @Test
  void updateIban_ibanCodesConstraintViolation_400() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(organizationFiscalCode, "fakeiban", iban));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  void updateIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    String ibanValue = "IT99C0222211111000000000000";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode)).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_noIbanFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "fakeCIFiscalCode";
    String ibanValue = "IT99C0222211111000000000000";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Pa creditorInstitution = getMockPa();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_noIbanCIRelationFound_404() { // retrieving mock object
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
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
    IbanMaster updatedMockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    updatedMockIbanMaster.setIbanStatus(IbanStatus.DISABLED);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updatePostalIbanByDifferentCI_noIbanCIRelationFound_404() { // retrieving mock object
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    Pa pa1 = getMockPa();
    String fc1 = pa1.getIdDominio();
    Pa pa2 = getMockPa2();
    Iban mockIban = getMockIban(iban, fc1);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    List<IbanMaster> ibanMasters = getMockIbanMasters(pa2, iban, mockIban, mockIcaBinaryFile);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(fc1))
        .thenReturn(Optional.of(pa1));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), eq(pa2.getObjId()))).thenReturn(ibanMasters);
    // return empty list because doesn't exist relation between pa1 and iban
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), eq(pa1.getObjId()))).thenReturn(List.of());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(fc1, iban.getIbanValue(), iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_invalidLabel_422() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String ibanValue = "IT99C0222211111000000000000";
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.getLabels().get(0).setName("FAKELABEL");
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IcaBinaryFile mockIcaBinaryFile = getEmptyMockIcaBinaryFile();
    IbanMaster mockIbanMaster =
        getMockIbanMaster(creditorInstitution, iban, mockIban, mockIcaBinaryFile);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .thenThrow(
            IllegalArgumentException
                .class); // forcing to throws exception if it generate an iban attribute relation
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () -> ibanService.updateIban(organizationFiscalCode, ibanValue, iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  private Iban getMockIban(IbanEnhanced iban, String organizationFiscalCode) {
    return Iban.builder()
        .objId(100L)
        .iban(iban.getIbanValue())
        .fiscalCode(organizationFiscalCode)
        .description(iban.getDescription())
        .dueDate(CommonUtil.toTimestamp(iban.getDueDate()))
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

  private IbanMaster getMockIbanMaster(
      Pa creditorInstitution,
      IbanEnhanced iban,
      Iban ibanToBeCreated,
      IcaBinaryFile icaBinaryFile) {
    return IbanMaster.builder()
        .objId(100L)
        .fkPa(creditorInstitution.getObjId())
        .pa(creditorInstitution)
        .fkIban(ibanToBeCreated.getObjId())
        .iban(ibanToBeCreated)
        .ibanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED)
        .insertedDate(CommonUtil.toTimestamp(OffsetDateTime.now(ZoneOffset.UTC)))
        .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
        .build();
  }

  @Test
  void deleteIban() {
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(getMockIbanEntity()));
    when(paRepository.findByIdDominio(any())).thenReturn(Optional.of(getMockPa()));
    when(ibanMasterRepository.findByFkIbanAndFkPa(any(), any()))
        .thenReturn(List.of(getMockIbanMaster_2()));
    when(ibanAttributeMasterRepository.findByFkIbanMasterIn(any()))
        .thenReturn(List.of(getMockIbanAttributeMaster()));

    ibanService.deleteIban("00168480242", "IT99C0222211111000000000000");
    Mockito.verify(ibanRepository, times(1)).findByIban("IT99C0222211111000000000000");
    Mockito.verify(paRepository, times(1)).findByIdDominio("00168480242");
    Mockito.verify(ibanMasterRepository, times(1)).findByFkIbanAndFkPa(1L, 1L);
    Mockito.verify(ibanAttributeMasterRepository, times(1)).findByFkIbanMasterIn(List.of(1L));
    assertTrue(true);
  }

  @Test
  void deleteIban_NotFound() {
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    try {
      ibanService.deleteIban("1234", "IT99C0222211111000000000000");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  public static List<IbanMaster> getMockIbanMasters(
      Pa creditorInstitution, IbanEnhanced iban, Iban ibanEntity, IcaBinaryFile icaBinaryFile) {
    List<IbanMaster> ibanMasters =
        List.of(
            IbanMaster.builder()
                .objId(100L)
                .fkPa(creditorInstitution.getObjId())
                .fkIban(ibanEntity.getObjId())
                .fkIcaBinaryFile(icaBinaryFile.getObjId())
                .icaBinaryFile(icaBinaryFile)
                .ibanStatus(iban.isActive() ? IbanStatus.ENABLED : IbanStatus.DISABLED)
                .insertedDate(
                    CommonUtil.toTimestamp(OffsetDateTime.parse("2023-05-23T10:38:07.165+02")))
                .validityDate(CommonUtil.toTimestamp(iban.getValidityDate()))
                .build());
    ibanMasters.get(0).setIbanAttributesMasters(getMockIbanAttributeMasters(ibanMasters.get(0)));

    return ibanMasters;
  }
}
