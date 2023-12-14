package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.*;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.CreditorInstitutionEncodings;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Encoding.CodeTypeEnum;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanEnhanced;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbanLabel;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.IbansEnhanced;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttribute;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster.IbanStatus;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;

@SpringBootTest(classes = ApiConfig.class)
class IbanServiceTest {

  @MockBean private PaRepository paRepository;

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private IbanAttributeRepository ibanAttributeRepository;

  @MockBean private IbanAttributeMasterRepository ibanAttributeMasterRepository;
  
  @MockBean private CodifichePaRepository codifichePaRepository;

  @MockBean private AzureStorageInteraction azureStorageInteraction;

  @MockBean private EncodingsService encodingsService;

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
    List<IbanMaster> mockIbanMasters = getMockIbanMasters(creditorInstitution, iban, mockIban);
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
    List<IbanMaster> mockIbanMasters = getMockIbanMasters(pa2, iban, mockIban);
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
    List<IbanMaster> mockIbanMasters = getMockIbanMasters(creditorInstitution, iban, mockIban);
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
    List<IbanMaster> mockIbanMasters = getMockIbanMasters(creditorInstitution, iban, mockIban);
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
    String organizationFiscalCode = "13229677908";
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
  void createPostalIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    Encoding encoding =
        Encoding.builder()
            .codeType(CodeTypeEnum.BARCODE_128_AIM)
            .encodingCode(
                iban.getIbanValue()
                    .substring(
                        iban.getIbanValue().length()
                            - 12)) // for BARCODE-128-AIM encoding code equals to last 12 characters
            // of iban value
            .build();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong())).thenReturn(List.of());
    when(ibanMasterRepository.save(any(IbanMaster.class))).thenReturn(mockIbanMaster);
    when(ibanAttributeRepository.findAll()).thenReturn(ibanAttributes);
    when(ibanAttributeMasterRepository.save(any(IbanAttributeMaster.class)))
        .then(returnsFirstArg());
    when(encodingsService.createCreditorInstitutionEncoding(
            creditorInstitution.getIdDominio(), encoding))
        .thenReturn(encoding);
    // executing logic and check assertions
    ibanService.createIban(organizationFiscalCode, iban);
    Mockito.verify(encodingsService, times(1))
        .createCreditorInstitutionEncoding(creditorInstitution.getIdDominio(), encoding);
  }

  @Test
  void createIban_existingIban_200() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    String otherOwnerOrganizationFiscalCode = "anotherCI";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Iban mockIban = getMockIban(iban, otherOwnerOrganizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    iban.setLabels(null);
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
  void createIban_400_1() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.createIban(organizationFiscalCode, iban));
  }

  @Test
  void createIban_400_2() {
    // retrieving mock object
    String organizationFiscalCode = "bad_fiscal_code_format";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue("IT99C0222211111000000000000");
    iban.setValidityDate(OffsetDateTime.now());
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.createIban(organizationFiscalCode, iban));
  }

  @Test
  void createIban_400_3() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue("bad_iban_value_format");
    iban.setValidityDate(OffsetDateTime.now());
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.createIban(organizationFiscalCode, iban));
  }
  
  @Test
  void createIban_400_4() {
	  Pa creditorInstitution = getMockPa();
	  String organizationFiscalCode = creditorInstitution.getIdDominio();
	  // validity date NOT greater than the today's date
	  IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now().plusDays(2));
	  Iban mockIban = getMockIban(iban, organizationFiscalCode);
	  IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
	  List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
	  // mocking responses from repositories
	  when(paRepository.findByIdDominio(organizationFiscalCode))
	  .thenReturn(Optional.of(creditorInstitution));
	  // executing logic and check assertions
	  assertThrows(
			  AppException.class,
			  () -> ibanService.createIban(organizationFiscalCode, iban));
  }
  
  @Test
  void createIban_400_5() {
	  Pa creditorInstitution = getMockPa();
	  String organizationFiscalCode = creditorInstitution.getIdDominio();
	  // due date NOT greater than the validity date
	  IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(2), OffsetDateTime.now().plusDays(2));
	  Iban mockIban = getMockIban(iban, organizationFiscalCode);
	  IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
	  List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
	  // mocking responses from repositories
	  when(paRepository.findByIdDominio(organizationFiscalCode))
	  .thenReturn(Optional.of(creditorInstitution));
	  // executing logic and check assertions
	  assertThrows(
			  AppException.class,
			  () -> ibanService.createIban(organizationFiscalCode, iban));
  }

  @Test
  void createIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
        .then(returnsFirstArg());
    when(ibanMasterRepository.findByFkIban(anyLong()))
        .thenReturn(getMockIbanMasters(creditorInstitution, iban, mockIban));
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    iban.getLabels().get(0).setName("FAKELABEL");
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // mocking responses from repositories
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    List<IbanAttribute> ibanAttributes = getMockIbanAttributes();
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
  void updateIban_genericConstraintViolation_400_1() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue(null);
    iban.setValidityDate(null);
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.updateIban(organizationFiscalCode, "IT99C0222211111000000000000", iban));
  }

  @Test
  void updateIban_genericConstraintViolation_400_2() {
    // retrieving mock object
    String organizationFiscalCode = "bad_fiscal_code_format";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue("IT99C0222211111000000000000");
    iban.setValidityDate(OffsetDateTime.now());
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.updateIban(organizationFiscalCode, "IT99C0222211111000000000000", iban));
  }

  @Test
  void updateIban_genericConstraintViolation_400_3() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    iban.setIbanValue("bad_iban_value_format");
    iban.setValidityDate(OffsetDateTime.now());
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.updateIban(organizationFiscalCode, "bad_iban_code", iban));
  }

  @Test
  void updateIban_ibanCodesConstraintViolation_400() {
    // retrieving mock object with ibanValue different from ibanCode
    String organizationFiscalCode = "13229677908";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () ->
                ibanService.updateIban(
                    organizationFiscalCode, "IT99C9999999999999999999999", iban));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }
  
  @Test
  void updateIban_IncorrectValidityDate_400() {
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now(), OffsetDateTime.now().plusDays(2));
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
    mockIbanMaster.setValidityDate(Timestamp.from(OffsetDateTime.now().plusDays(2).toInstant()));
    // mocking responses from repositories
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(paRepository.findByIdDominio(organizationFiscalCode))
            .thenReturn(Optional.of(creditorInstitution));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), anyLong()))
            .thenReturn(List.of(mockIbanMaster));
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () ->
                ibanService.updateIban(
                    organizationFiscalCode, "IT99C0222211111000000000000", iban));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }
  
  @Test
  void updateIban_IncorrectDueDate_400() {
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(1));
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    mockIban.setDueDate(Timestamp.from(OffsetDateTime.now().toInstant()));
    // mocking responses from repositories
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(paRepository.findByIdDominio(organizationFiscalCode))
        .thenReturn(Optional.of(creditorInstitution));
    
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class,
            () ->
                ibanService.updateIban(
                    organizationFiscalCode, "IT99C0222211111000000000000", iban));
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }
 

  @Test
  void updateIban_noCIFound_404() {
    // retrieving mock object
    String organizationFiscalCode = "13229677908";
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
    String organizationFiscalCode = "13229677908";
    String ibanValue = "IT99C0222211111000000000000";
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
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
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    String ibanValue = "IT99C0222211111000000000000";
    Pa creditorInstitution = getMockPa();
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    IbanMaster updatedMockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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
    IbanEnhanced iban = getMockPostalIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    Pa pa1 = getMockPa();
    String fc1 = pa1.getIdDominio();
    Pa pa2 = getMockPa2();
    Iban mockIban = getMockIban(iban, fc1);
    // generating updated mock iban object
    iban.setDescription("Edited description");
    iban.setLabels(List.of(iban.getLabels().get(0)));
    iban.setActive(false);
    iban.setLabels(null);
    List<IbanMaster> ibanMasters = getMockIbanMasters(pa2, iban, mockIban);
    // mocking responses from repositories
    when(paRepository.findByIdDominio(fc1)).thenReturn(Optional.of(pa1));
    when(ibanRepository.save(any(Iban.class))).thenReturn(mockIban);
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(mockIban));
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), eq(pa2.getObjId())))
        .thenReturn(ibanMasters);
    // return empty list because doesn't exist relation between pa1 and iban
    when(ibanMasterRepository.findByFkIbanAndFkPa(anyLong(), eq(pa1.getObjId())))
        .thenReturn(List.of());
    // executing logic and check assertions
    AppException ex =
        assertThrows(
            AppException.class, () -> ibanService.updateIban(fc1, iban.getIbanValue(), iban));
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void updateIban_invalidLabel_422() {
    // retrieving mock object
    Pa creditorInstitution = getMockPa();
    String ibanValue = "IT99C0222211111000000000000";
    String organizationFiscalCode = creditorInstitution.getIdDominio();
    IbanEnhanced iban = getMockIbanEnhanced(OffsetDateTime.now().plusDays(1), OffsetDateTime.now().plusDays(2));
    iban.getLabels().get(0).setName("FAKELABEL");
    Iban mockIban = getMockIban(iban, organizationFiscalCode);
    IbanMaster mockIbanMaster = getMockIbanMaster(creditorInstitution, iban, mockIban);
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

  @Test
  void deleteIban() {
    when(ibanRepository.findByIban(anyString()))
        .thenReturn(Optional.of(getMockIbanEntity("IT99C0222211111000000000000")));
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
  void deletePostalIban() {
    String organizationFiscalCode = getMockPa().getIdDominio();
    Iban iban = getMockIbanEntity("IT99C0760111111000000000000");
    String ibanCode = iban.getIban();
    String encodingCode = ibanCode.substring(ibanCode.length() - 12);
    Encoding encoding =
        Encoding.builder()
            .codeType(CodeTypeEnum.BARCODE_128_AIM)
            .encodingCode(encodingCode)
            .build();
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(iban));
    when(paRepository.findByIdDominio(any())).thenReturn(Optional.of(getMockPa()));
    when(ibanMasterRepository.findByFkIbanAndFkPa(any(), any()))
        .thenReturn(List.of(getMockIbanMaster_2()));
    when(ibanAttributeMasterRepository.findByFkIbanMasterIn(any()))
        .thenReturn(List.of(getMockIbanAttributeMaster()));
    doNothing()
        .when(encodingsService)
        .deleteCreditorInstitutionEncoding(organizationFiscalCode, encodingCode);
    when(encodingsService.getCreditorInstitutionEncodings(organizationFiscalCode))
        .thenReturn(CreditorInstitutionEncodings.builder().encodings(List.of(encoding)).build());
    ibanService.deleteIban(organizationFiscalCode, ibanCode);
    Mockito.verify(ibanRepository, times(1)).findByIban(ibanCode);
    Mockito.verify(paRepository, times(1)).findByIdDominio(organizationFiscalCode);
    Mockito.verify(ibanMasterRepository, times(1)).findByFkIbanAndFkPa(1L, 1L);
    Mockito.verify(ibanAttributeMasterRepository, times(1)).findByFkIbanMasterIn(List.of(1L));
    Mockito.verify(encodingsService, times(1))
        .getCreditorInstitutionEncodings(organizationFiscalCode);
    Mockito.verify(encodingsService, times(1))
        .deleteCreditorInstitutionEncoding(organizationFiscalCode, encodingCode);
  }

  @Test
  void deleteIban_NotFound() {
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    try {
      ibanService.deleteIban("13229677908", "IT99C0222211111000000000000");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void deleteIban_400_1() {
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.deleteIban("bad_fiscal_code_format", "IT99C9999999999999999999999"));
  }

  @Test
  void deleteIban_400_2() {
    // executing logic and check assertions
    assertThrows(
        ConstraintViolationException.class,
        () -> ibanService.deleteIban("13229677908", "bad_iban_value_format"));
  }

    @Test
    void getIbansEnhancedFilteredACA_200()
            throws IOException,
            JSONException,
            org.springframework.boot.configurationprocessor.json.JSONException {
        // retrieving mock object
        Pa creditorInstitution = getMockPa();
        String organizationFiscalCode = creditorInstitution.getIdDominio();
        Iban mockIban = getMockIban(organizationFiscalCode);
        LocalDateTime nowTime = LocalDateTime.now();
        IbanMaster element1 = getMockIbanMasterValidityDateInsertedDate(creditorInstitution, mockIban, Timestamp.valueOf(nowTime.minusYears(1)), Timestamp.valueOf(nowTime.minusYears(1)));
        IbanMaster element2 = getMockIbanMasterValidityDateInsertedDate(creditorInstitution, mockIban, Timestamp.valueOf(nowTime.minusYears(1)), Timestamp.valueOf(nowTime.minusYears(2)));
        element1.setIbanAttributesMasters(getMockIbanAttributeMasters(element1));
        element2.setIbanAttributesMasters(getMockIbanAttributeMasters(element2));
        List<IbanMaster> mockIbanMasters = Arrays.asList(element1, element2);
        creditorInstitution.setIbanMasters(mockIbanMasters);
        // mocking responses from repositories
        when(paRepository.findByIdDominio(organizationFiscalCode))
                .thenReturn(Optional.of(creditorInstitution));
        when(ibanMasterRepository.findByFkPa(creditorInstitution.getObjId()))
                .thenReturn(mockIbanMasters);
        when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
        // executing logic and check assertions
        IbansEnhanced result =
                ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, "testAca");

        assertEquals(1, result.getIbanEnhancedList().size());
        assertEquals(nowTime.minusYears(1), result.getIbanEnhancedList().get(0).getPublicationDate().toLocalDateTime());
        assertEquals("IT99C0222211111000000000004", result.getIbanEnhancedList().get(0).getIbanValue());
    }

    @Test
    void getIbansEnhancedFilteredACANoValid_200()
            throws IOException,
            JSONException,
            org.springframework.boot.configurationprocessor.json.JSONException {
        // retrieving mock object
        Pa creditorInstitution = getMockPa();
        String organizationFiscalCode = creditorInstitution.getIdDominio();
        Iban mockIban = getMockIban(organizationFiscalCode);
        LocalDateTime nowTime = LocalDateTime.now();
        IbanMaster element1 = getMockIbanMasterValidityDateInsertedDate(creditorInstitution, mockIban, Timestamp.valueOf(nowTime.plusYears(1)), Timestamp.valueOf(nowTime.plusYears(1)));
        IbanMaster element2 = getMockIbanMasterValidityDateInsertedDate(creditorInstitution, mockIban, Timestamp.valueOf(nowTime.plusYears(1)), Timestamp.valueOf(nowTime.plusYears(2)));
        element1.setIbanAttributesMasters(getMockIbanAttributeMasters(element1));
        element2.setIbanAttributesMasters(getMockIbanAttributeMasters(element2));
        List<IbanMaster> mockIbanMasters = Arrays.asList(element1, element2);
        creditorInstitution.setIbanMasters(mockIbanMasters);
        // mocking responses from repositories
        when(paRepository.findByIdDominio(organizationFiscalCode))
                .thenReturn(Optional.of(creditorInstitution));
        when(ibanMasterRepository.findByFkPa(creditorInstitution.getObjId()))
                .thenReturn(mockIbanMasters);
        when(ibanRepository.findById(anyLong())).thenReturn(Optional.of(mockIban));
        // executing logic and check assertions
        IbansEnhanced result =
                ibanService.getCreditorInstitutionsIbansByLabel(organizationFiscalCode, "testAca");

        assertEquals(0, result.getIbanEnhancedList().size());
    }
    
    @Test
    void massiveCreateIbans_ok() throws IOException {
    	File zip = TestUtil.readFile("file/massiveIbansValid.zip");
    	MockMultipartFile file =
    			new MockMultipartFile(
    					"file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    	when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    	when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
    	try {
    		ibanService.createMassiveIbans(file);
    	} catch (Exception e) {
    		fail(e);
    	}
    }
    
    @Test
    void massiveCreateIbans_ko() throws IOException {
    	File zip = TestUtil.readFile("file/massiveIbansBad.zip");
    	MockMultipartFile file =
    			new MockMultipartFile(
    					"file",
    					zip.getName(),
    					MediaType.MULTIPART_FORM_DATA_VALUE,
    					new FileInputStream(zip));
    	when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    	when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
    	.thenReturn(Lists.list(getMockCodifichePa()));
    	try {
    		ibanService.createMassiveIbans(file);
    		fail();
    	} catch (AppException e) {
    		assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    	}
    }
    
    @Test
    void massiveCreateIbansByCsv_ok() throws IOException {
    	
    	when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    	when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
    	when(ibanMasterRepository.findByFkIbanAndFkPa(any(), any())).thenReturn(List.of(getMockIbanMaster_2()));
        
    	File zip = TestUtil.readFile("file/massiveIbansValid_Insert.csv");
    	MockMultipartFile file =
    			new MockMultipartFile(
    					"file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    	try {
    		ibanService.createMassiveIbansByCsv(file);
    	} catch (Exception e) {
    		fail(e);
    	}
    	
    	Optional<Iban> ibanEntity = Optional.of(Iban.builder().iban("1234567898000").description("mock").build());
    	when(ibanRepository.findByIban(anyString())).thenReturn(ibanEntity);
    	
    	zip = TestUtil.readFile("file/massiveIbansValid_Update_Delete.csv");
    	file =
    			new MockMultipartFile(
    					"file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    	try {
    		ibanService.createMassiveIbansByCsv(file);
    	} catch (Exception e) {
    		fail(e);
    	}
    }
    
    @Test
    void massiveCreateIbansByCsv_ko() throws IOException {
    	
    	when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    	when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
        
    	File zip = TestUtil.readFile("file/massiveIbansValid_Bad.csv");
    	MockMultipartFile file =
    			new MockMultipartFile(
    					"file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    	try {
    		ibanService.createMassiveIbansByCsv(file);
    		fail();
    	} catch (AppException e) {
    		assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    	}
    }

  @Test
  void upsertIbanLabel_insert_200() throws JsonProcessingException {
    String name = "fakelabel";
    String description = "description edit";
    IbanLabel ibanLabel = new IbanLabel(name, description);
    IbanAttribute changedIbanAttribute = IbanAttribute.builder().objId(1L).attributeName(name).attributeDescription(description).build();

    when(ibanAttributeRepository.findAll()).thenReturn(List.of());
    when(ibanAttributeRepository.save(any(IbanAttribute.class))).thenReturn(changedIbanAttribute);

    IbanLabel response = ibanService.upsertIbanLabel(ibanLabel);

    assertEquals(TestUtil.toJson(response), TestUtil.toJson(ibanLabel));
  }

  @Test
  void upsertIbanLabel_insertWithOtherLabel_200() throws JsonProcessingException {
    String name = "fakelabel";
    String description = "description edit";
    IbanLabel ibanLabel = new IbanLabel(name, description);
    IbanAttribute changedIbanAttribute = IbanAttribute.builder().objId(1L).attributeName(name).attributeDescription(description).build();
    List<IbanAttribute> mockIbanAttributes = List.of(
            IbanAttribute.builder().objId(1L).attributeName("label1").attributeDescription("description-1").build(),
            IbanAttribute.builder().objId(2L).attributeName("label2").attributeDescription("description-2").build());

    when(ibanAttributeRepository.findAll()).thenReturn(mockIbanAttributes);
    when(ibanAttributeRepository.save(any(IbanAttribute.class))).thenReturn(changedIbanAttribute);

    IbanLabel response = ibanService.upsertIbanLabel(ibanLabel);

    assertEquals(TestUtil.toJson(response), TestUtil.toJson(ibanLabel));
  }

  @Test
  void upsertIbanLabel_update_200() throws JsonProcessingException {
    Long id = 1L;
    String name = "fakelabel";
    String description = "description edit";
    IbanLabel ibanLabel = new IbanLabel(name, description);
    IbanAttribute mockIbanAttribute = IbanAttribute.builder().objId(id).attributeName(name).attributeDescription("no-description").build();
    IbanAttribute changedIbanAttribute = IbanAttribute.builder().objId(id).attributeName(name).attributeDescription(description).build();

    when(ibanAttributeRepository.findAll()).thenReturn(List.of(mockIbanAttribute));
    when(ibanAttributeRepository.save(any(IbanAttribute.class))).thenReturn(changedIbanAttribute);

    IbanLabel response = ibanService.upsertIbanLabel(ibanLabel);

    assertEquals(TestUtil.toJson(response), TestUtil.toJson(ibanLabel));
    Assertions.assertNotEquals(TestUtil.toJson(response), TestUtil.toJson(mockIbanAttribute));
  }
}
