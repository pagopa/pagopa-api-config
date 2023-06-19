package it.gov.pagopa.apiconfig.core.scheduler;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEntity;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanMaster_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.starter.entity.IcaBinaryFile;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IcaBinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.microsoft.azure.storage.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.exception.AppException;

@Slf4j
@SpringBootTest(classes = ApiConfig.class)
class SchedulerTest {

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private PaRepository paRepository;

  @MockBean private AzureStorageInteraction azureStorageInteraction;

  @Autowired private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired @InjectMocks private SchedulerIca schedulerIca;


  @Test
  void checkSchedulerAction() throws StorageException, InterruptedException {

    String x = null;
    when(paRepository.findByIdDominioIn(any(List.class))).thenReturn(Optional.of(List.of(getMockPa())));
    Map<String, String> mockMap = new HashMap<>();
    mockMap.put("00168480242", LocalDateTime.now().plusMinutes(10).toString());
    when(azureStorageInteraction.getUpdatedEC(anyString())).thenReturn(mockMap);
    when(ibanRepository.findByObjIdIn(any(List.class))).thenReturn(List.of(getMockIbanEntity()));
    when(ibanMasterRepository.findByFkPa(any(Long.class))).thenReturn(List.of(getMockIbanMaster_2()));

    // Funzione schedulata chiamata staticamente
    schedulerIca.updateIcaFile();

    // Check che il file iban sia stato salvato
    Thread.sleep(1000);
    IcaBinaryFile icaBinaryFile = icaBinaryFileRepository
        .findByIdDominio("00168480242")
        .orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, "00168480242"));
    String xml = new String(icaBinaryFile.getFileContent());
    assertThat(xml, containsString(LocalDateTime.now().toLocalDate().toString()));
    assertThat(xml, containsString(getMockIbanEntity().getIban()));
  }
}

