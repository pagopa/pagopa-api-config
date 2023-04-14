package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCdis;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.service.CdiService;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class CdiControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CdiService cdiService;

  @BeforeEach
  void setUp() {
    when(cdiService.getCdis(50, 0, null, null)).thenReturn(getMockCdis());
    when(cdiService.getCdi(anyString(), anyString())).thenReturn(new byte[] {});
  }

  @Test
  void getCdis() throws Exception {
    String url = "/cdis?page=0";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCdi() throws Exception {
    String url = "/cdis/1234?pspcode=1234ABC12345";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_XML));
  }

  @Test
  void createCdi() throws Exception {
    File xml = TestUtil.readFile("file/cdi_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/cdis";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void deleteCdi() throws Exception {
    mvc.perform(delete("/cdis/1234?pspcode=1234ABC12345").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void checkCdi() throws Exception {
    File xml = TestUtil.readFile("file/cdi_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/cdis/check";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk());
  }

  @Test
  void uploadHistory() throws Exception {
    String url = "/cdis/history";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }
}
