package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockElencoServizi;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

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

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.model.psp.Service;
import it.gov.pagopa.apiconfig.core.model.psp.Services;
import it.gov.pagopa.apiconfig.core.service.ServicesService;
import it.gov.pagopa.apiconfig.starter.entity.ElencoServizi;
import it.gov.pagopa.apiconfig.starter.repository.ElencoServiziRepository;

@SpringBootTest(classes = ApiConfig.class)
class ServicesServiceTest {

  @MockBean private ElencoServiziRepository elencoServiziRepository;

  @Autowired @InjectMocks private ServicesService servicesService;

  @Test
  void getServices() throws IOException, JSONException {
    Page<ElencoServizi> page = TestUtil.mockPage(Lists.newArrayList(getMockElencoServizi()), 50, 0);
    when(elencoServiziRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    Services result = servicesService.getServices(50, 0, Service.Filter.builder().build());

    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_services_ok1.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }

  @Test
  void getServices_filter() throws IOException, JSONException {
    Page<ElencoServizi> page = TestUtil.mockPage(Lists.newArrayList(getMockElencoServizi()), 50, 0);
    when(elencoServiziRepository.findAll(any(), any(Pageable.class))).thenReturn(page);

    Services result =
        servicesService.getServices(
            50, 0, Service.Filter.builder().languageCode(Service.LanguageCode.EN).build());
    String actual = TestUtil.toJson(result);
    String expected = TestUtil.readJsonFromFile("response/get_services_ok2.json");
    JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
  }
}
