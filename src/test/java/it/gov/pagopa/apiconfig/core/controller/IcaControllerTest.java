package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIcas;
import static it.gov.pagopa.apiconfig.TestUtil.getMockXSDValidation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.service.IcaService;
import java.io.File;
import java.io.FileInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class IcaControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private IcaService icaService;

  @BeforeEach
  void setUp() {
    when(icaService.getIcas(50, 0, null, null)).thenReturn(getMockIcas());
    when(icaService.getIca(anyString(), anyString())).thenReturn(new byte[] {});
    when(icaService.verifyXSD(any())).thenReturn(getMockXSDValidation());
  }

  @Test
  void getIcas() throws Exception {
    String url = "/icas?page=0";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getIca() throws Exception {
    String url = "/icas/123?creditorinstitutioncode=1";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_XML));
  }

  @Test
  void checkXML() throws Exception {
    File xml = TestUtil.readFile("file/ica_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/icas/xsd";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void createIca() throws Exception {
    File xml = TestUtil.readFile("file/ica_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/icas";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void deleteIca() throws Exception {
    mvc.perform(
            delete("/icas/1234?creditorinstitutioncode=1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void verifyIca() throws Exception {
    File xml = TestUtil.readFile("file/ica_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/icas/check";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void massiveVerifyIcas() throws Exception {
    File zip = TestUtil.readFile("file/massiveIca.zip");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    String url = "/icas/check/massive";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void massiveCreateIcas() throws Exception {
    File zip = TestUtil.readFile("file/massiveIca.zip");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", zip.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(zip));
    String url = "/icas/massive";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }
}
