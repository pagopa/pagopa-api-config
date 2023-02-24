package it.pagopa.pagopa.apiconfig.service;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBinaryFile;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCodifichePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativeContoAccreditoMaster;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CheckItem;
import it.pagopa.pagopa.apiconfig.model.MassiveCheck;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Encoding;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.XSDValidation;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.assertj.core.util.Lists;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest(classes = ApiConfig.class)
class IcaServiceTest {

  @MockBean InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

  @MockBean PaRepository paRepository;

  @MockBean CodifichePaRepository codifichePaRepository;

  @MockBean BinaryFileRepository binaryFileRepository;

  @MockBean InformativeContoAccreditoDetailRepository informativeContoAccreditoDetailRepository;

  @MockBean IbanValidiPerPaRepository ibanValidiPerPaRepository;

  @Autowired @InjectMocks IcaService icaService;

  @Test
  void getIcas() throws JSONException, IOException {
    Page<InformativeContoAccreditoMaster> page =
        TestUtil.mockPage(Lists.newArrayList(getMockInformativeContoAccreditoMaster()), 50, 0);
    when(informativeContoAccreditoMasterRepository.findAll(any(), any(Pageable.class)))
        .thenReturn(page);

    Icas result = icaService.getIcas(50, 0, null, null);
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_icas_ok.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getIca() {
    when(informativeContoAccreditoMasterRepository
            .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString()))
        .thenReturn(Optional.of(getMockInformativeContoAccreditoMaster()));

    byte[] result = icaService.getIca("111", "1234");
    assertNotNull(result);
    assertEquals(2, result.length);
  }

  @Test
  void getIca_NotFound() {
    when(informativeContoAccreditoMasterRepository
            .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString()))
        .thenReturn(Optional.empty());
    try {
      icaService.getIca("111", "1234");
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void checkValidXML() throws IOException, JSONException {
    File xml = TestUtil.readFile("file/ica_valid.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    XSDValidation result = icaService.verifyXSD(file);
    String expected = TestUtil.readJsonFromFile("response/ica_valid_xml.json");
    JSONAssert.assertEquals(expected, TestUtil.toJson(result), JSONCompareMode.STRICT);
  }

  //    @Test
  //    void checkNotValidXML() throws IOException, JSONException {
  //        File xml = TestUtil.readFile("file/ica_xsd_not_valid.xml");
  //        MockMultipartFile file = new MockMultipartFile("file", xml.getName(),
  // MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
  //        XSDValidation result = icaService.verifyXSD(file);
  //        String expected = TestUtil.readJsonFromFile("response/ica_xsd_not_valid_xml.json");
  //        JSONAssert.assertEquals(expected, TestUtil.toJson(result), JSONCompareMode.STRICT);
  //    }

  @Test
  void createIca() throws IOException {
    File xml = TestUtil.readFile("file/ica_valid_h2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    try {
      icaService.createIca(file, false);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void createIca_ko() throws IOException {
    File xml = TestUtil.readFile("file/ica_date_not_valid_h2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    try {
      icaService.createIca(file, false);
      fail();
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    }
  }

  @Test
  void deleteIca() {
    when(informativeContoAccreditoMasterRepository
            .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString()))
        .thenReturn(Optional.of(getMockInformativeContoAccreditoMaster()));
    try {
      icaService.deleteIca("1234", "2");
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void verifyIca_ok_1() throws IOException {
    File xml = TestUtil.readFile("file/ica_valid_h2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));

    List<CheckItem> checkItemList = icaService.verifyIca(file, false);

    assertFalse(
        checkItemList.stream()
            .anyMatch(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)));
  }

  // xsd not valid
  @Test
  void verifyIca_ko_1() throws IOException {
    File xml = TestUtil.readFile("file/ica_xsd_not_valid.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));

    List<CheckItem> checkItemList = icaService.verifyIca(file, false);
    assertEquals(
        1,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .count());
  }

  // creditor institution not consistent
  // validityDate not consistent
  @Test
  void verifyIca_ko_2() throws IOException {
    File xml = TestUtil.readFile("file/ica_info_not_valid_h2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.empty());

    List<CheckItem> checkItemList = icaService.verifyIca(file, false);
    assertEquals(
        2,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .count());
  }

  // qr-code not present
  // flow ìdentifier present
  @Test
  void verifyIca_ko_3() throws IOException {
    File xml = TestUtil.readFile("file/ica_info_not_valid_h2.xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));

    CodifichePa barcode128aim =
        CodifichePa.builder()
            .id(1L)
            .codicePa("000000001016")
            .fkCodifica(
                Codifiche.builder()
                    .objId(2L)
                    .idCodifica(Encoding.CodeTypeEnum.BARCODE_128_AIM.getValue())
                    .build())
            .build();

    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(List.of(barcode128aim));
    when(informativeContoAccreditoMasterRepository
            .findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString()))
        .thenReturn(Optional.of(getMockInformativeContoAccreditoMaster()));

    IbanValidiPerPa iban_1 =
        IbanValidiPerPa.builder().ibanAccredito("IT04I0103061821000000248378").build();

    IbanValidiPerPa iban_2 =
        IbanValidiPerPa.builder().ibanAccredito("IT45R0760103200000000001016").build();

    List<IbanValidiPerPa> ibans = List.of(iban_1, iban_2);

    when(ibanValidiPerPaRepository.findAllByFkPa(anyLong())).thenReturn(ibans);

    List<CheckItem> checkItemList = icaService.verifyIca(file, false);
    assertEquals(
        3,
        checkItemList.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .count());
  }

  @Test
  void verifyIca_ko_4() throws IOException {
    File tempFile = File.createTempFile("placeholder", "xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file",
            tempFile.getName(),
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new FileInputStream(tempFile));
    List<CheckItem> list = icaService.verifyIca(file, false);
    assertEquals(
        1,
        list.stream().filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)).count());
  }

  @Test
  void massiveVerifyIca_ok() throws IOException {
    File zip = TestUtil.readFile("file/massiveIca.zip");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));

    List<MassiveCheck> massiveChecks = icaService.massiveVerifyIcas(file, false);
    List<CheckItem> list1 = massiveChecks.get(0).getCheckItems();
    List<CheckItem> list2 = massiveChecks.get(1).getCheckItems();

    assertFalse(
        list1.stream().anyMatch(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)));
    assertEquals(
        9,
        list2.stream()
            .filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID))
            .count());
  }

  @Test
  void massiveCreateIca_ok() throws IOException {
    File zip = TestUtil.readFile("file/massiveIcaValid.zip");
    MockMultipartFile file =
        new MockMultipartFile(
            "file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    try {
      icaService.createMassiveIcas(file);
    } catch (Exception e) {
      fail(e);
    }
  }

  @Test
  void massiveCreateIca_ko() throws IOException {
    File tempFile = File.createTempFile("placeholder", "xml");
    MockMultipartFile file =
        new MockMultipartFile(
            "file",
            tempFile.getName(),
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new FileInputStream(tempFile));
    when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
    when(codifichePaRepository.findAllByFkPa_ObjId(anyLong()))
        .thenReturn(Lists.list(getMockCodifichePa()));
    when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
    try {
      icaService.createMassiveIcas(file);
    } catch (AppException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
    }
  }
}
