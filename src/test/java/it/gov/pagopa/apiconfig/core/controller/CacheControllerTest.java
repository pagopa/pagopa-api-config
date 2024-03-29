package it.gov.pagopa.apiconfig.core.controller;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCache;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCacheVersions;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class CacheControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CacheService cacheService;

  @BeforeEach
  void setUp() {
    when(cacheService.getCacheVersions(anyInt(), anyInt())).thenReturn(getMockCacheVersions());
    when(cacheService.getCache(anyString())).thenReturn(getMockCache());
  }

  @Test
  void getCacheVersions() throws Exception {
    String url = "/cache/versions";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void getCacheByVersion() throws Exception {
    String url = "/cache/versions/3.1.0";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_OCTET_STREAM))
        .andExpect(status().isOk());
  }

  @Test
  void getCacheById() throws Exception {
    String url = "/cache/versions/3.1.0/id";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }
}
