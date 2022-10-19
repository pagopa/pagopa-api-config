package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CheckItem;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.XSDValidation;
import it.pagopa.pagopa.apiconfig.repository.BinaryFileRepository;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoDetailRepository;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockBinaryFile;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCodifichePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockInformativeContoAccreditoMaster;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class IcaServiceTest {

    @MockBean
    InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

    @MockBean
    PaRepository paRepository;

    @MockBean
    CodifichePaRepository codifichePaRepository;

    @MockBean
    BinaryFileRepository binaryFileRepository;

    @MockBean
    InformativeContoAccreditoDetailRepository informativeContoAccreditoDetailRepository;

    @Autowired
    @InjectMocks
    IcaService icaService;

    @Test
    void getIcas() throws JSONException, IOException {
        Page<InformativeContoAccreditoMaster> page = TestUtil.mockPage(Lists.newArrayList(getMockInformativeContoAccreditoMaster()), 50, 0);
        when(informativeContoAccreditoMasterRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

        Icas result = icaService.getIcas(50, 0, null, null);
        String actual = TestUtil.toJson(result);
        String expected = TestUtil.readJsonFromFile("response/get_icas_ok.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void getIca() {
        when(informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString())).thenReturn(Optional.of(getMockInformativeContoAccreditoMaster()));

        byte[] result = icaService.getIca("111", "1234");
        assertNotNull(result);
        assertEquals(2, result.length);
    }


    @Test
    void getIca_NotFound() {
        when(informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString())).thenReturn(Optional.empty());
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
        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
        XSDValidation result = icaService.verifyXSD(file);
        String expected = TestUtil.readJsonFromFile("response/ica_valid_xml.json");
        JSONAssert.assertEquals(expected, TestUtil.toJson(result), JSONCompareMode.STRICT);
    }

//    @Test
//    void checkNotValidXML() throws IOException, JSONException {
//        File xml = TestUtil.readFile("file/ica_not_valid.xml");
//        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
//        XSDValidation result = icaService.verifyXSD(file);
//        String expected = TestUtil.readJsonFromFile("response/ica_not_valid_xml.json");
//        JSONAssert.assertEquals(expected, TestUtil.toJson(result), JSONCompareMode.STRICT);
//    }

    @Test
    void createIca() throws IOException {
        File xml = TestUtil.readFile("file/ica_valid_h2.xml");
        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
        when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
        try {
            icaService.createIca(file);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void createIca_ko() throws IOException {
        File xml = TestUtil.readFile("file/ica_date_not_valid_h2.xml");
        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
        when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());
        try {
            icaService.createIca(file);
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        }
    }


    @Test
    void deleteIca() {
        when(informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(anyString(), anyString()))
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
        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
        when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());

        List<CheckItem> checkItemList = icaService.verifyIca(file);

        assertFalse(checkItemList.stream().anyMatch(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)));
    }

    // xsd not valid
    @Test
    void verifyIca_ko_1() throws IOException {
        File xml = TestUtil.readFile("file/ica_not_valid.xml");
        MockMultipartFile file = new MockMultipartFile("file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(codifichePaRepository.findAllByFkPa_ObjId(anyLong())).thenReturn(Lists.list(getMockCodifichePa()));
        when(binaryFileRepository.save(any())).thenReturn(getMockBinaryFile());

        List<CheckItem> checkItemList = icaService.verifyIca(file);
        assertEquals(1,checkItemList.stream().filter(item -> item.getValid().equals(CheckItem.Validity.NOT_VALID)).count());
    }

}
