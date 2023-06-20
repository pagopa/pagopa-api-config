package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCounterpartTables;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.service.CounterpartService;
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
class CounterpartControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CounterpartService counterpartService;

  @BeforeEach
  void setUp() {
    when(counterpartService.getCounterpartTables(50, 0, null, null))
        .thenReturn(getMockCounterpartTables());
    when(counterpartService.getCounterpartTable(anyString(), anyString()))
        .thenReturn(new byte[] {});
  }

  @Test
  void getCounterpartTables() throws Exception {
    String url = "/counterparttables?page=0";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getCounterpartTable() throws Exception {
    String url = "/counterparttables/123?creditorinstitutioncode=111";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_XML));
  }

  @Test
  void createCounterpartTable() throws Exception {
    File xml = TestUtil.readFile("file/counterpart_valid.xml");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", xml.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(xml));
    String url = "/counterparttables";

    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void deleteCounterpartTable() throws Exception {
    mvc.perform(
            delete("/counterparttables/1234?creditorinstitutioncode=1")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
