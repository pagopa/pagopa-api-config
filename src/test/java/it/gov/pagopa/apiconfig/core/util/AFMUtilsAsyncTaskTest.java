package it.gov.pagopa.apiconfig.core.util;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCdiMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockCdiMasterValid;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.CdiMasterValid;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterValidRepository;
import it.pagopa.pagopa.apiconfig.client.AFMUtilsClient;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.util.AFMUtilsAsyncTask;
import it.gov.pagopa.apiconfig.core.util.AFMUtilsClient;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.repository.CdiMasterValidRepository;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCdiMaster;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCdiMasterValid;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class AFMUtilsAsyncTaskTest {
  @MockBean private CdiMasterValidRepository cdiMasterValidRepository;

  @MockBean private ModelMapper modelMapper;
  @MockBean private AFMUtilsClient afmUtilsClient;

  @Autowired private AFMUtilsAsyncTask afmUtilsAsyncTask;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(afmUtilsAsyncTask, "afmUtilsClient", afmUtilsClient);
  }

  @Test
  void executeSync() {
    Page<CdiMasterValid> page =
        TestUtil.mockPage(Lists.newArrayList(getMockCdiMasterValid()), 1, 0);
    when(cdiMasterValidRepository.findAll(any(PageRequest.class))).thenReturn(page);

    doNothing().when(afmUtilsClient).syncPaymentTypes(anyString(), anyString(), any());

    assertTrue(afmUtilsAsyncTask.executeSync());
  }

  @Test
  void executeSync2() {
    Page<CdiMasterValid> page =
        TestUtil.mockPage(Lists.newArrayList(getMockCdiMasterValid()), 1, 0);
    when(cdiMasterValidRepository.findAll(any(PageRequest.class))).thenReturn(page);

    doNothing().when(afmUtilsClient).syncPaymentTypes(anyString(), anyString(), any());

    assertTrue(afmUtilsAsyncTask.executeSync(getMockCdiMaster()));
  }
}
