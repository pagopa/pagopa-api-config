package it.gov.pagopa.apiconfig.core.scheduler;

import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEntity;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanMaster_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIcaBinaryFile_2;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.microsoft.azure.storage.StorageException;
import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

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
  void checkSchedulerAction_NewPa() throws StorageException, InterruptedException {

    when(paRepository.findByIdDominioIn(any(List.class)))
        .thenReturn(Optional.of(List.of(getMockPa())));
    Map<String, String> mockMap = new HashMap<>();
    mockMap.put("00168480242", LocalDateTime.now().plusMinutes(10).toString());
    when(azureStorageInteraction.getUpdatedEC(anyString())).thenReturn(mockMap);
    when(ibanRepository.findByObjIdIn(any(List.class))).thenReturn(List.of(getMockIbanEntity()));
    when(ibanMasterRepository.findByFkPa(any(Long.class)))
        .thenReturn(List.of(getMockIbanMaster_2()));

    // Funzione schedulata chiamata staticamente
    schedulerIca.updateIcaFile();

    // Check che il file iban sia stato salvato
    IcaBinaryFile icaBinaryFile =
        icaBinaryFileRepository
            .findByIdDominio("00168480242")
            .orElseThrow(
                () -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, "00168480242"));
    String xml = new String(icaBinaryFile.getFileContent());
    assertThat(xml, containsString(LocalDateTime.now().toLocalDate().toString()));
    assertThat(xml, containsString(getMockIbanEntity().getIban()));
  }

  @Test
  void checkSchedulerAction_ExistingPa() throws StorageException, InterruptedException {

    when(paRepository.findByIdDominioIn(any(List.class)))
        .thenReturn(Optional.of(List.of(getMockPa2())));
    Map<String, String> mockMap = new HashMap<>();
    mockMap.put("00168480243", LocalDateTime.now().plusMinutes(10).toString());
    when(azureStorageInteraction.getUpdatedEC(anyString())).thenReturn(mockMap);
    when(ibanRepository.findByObjIdIn(any(List.class))).thenReturn(List.of(getMockIbanEntity()));
    when(ibanMasterRepository.findByFkPa(any(Long.class)))
        .thenReturn(List.of(getMockIbanMaster_2()));

    // Inserimento ica binary file
    icaBinaryFileRepository.save(getMockIcaBinaryFile_2());

    // Funzione schedulata chiamata staticamente
    schedulerIca.updateIcaFile();

    // Check che il file iban sia stato salvato
    IcaBinaryFile icaBinaryFile =
        icaBinaryFileRepository
            .findByIdDominio("00168480243")
            .orElseThrow(
                () -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, "00168480243"));
    String xml = new String(icaBinaryFile.getFileContent());
    assertThat(xml, containsString("00168480243"));
    assertThat(xml, containsString(LocalDateTime.now().toLocalDate().toString()));
    assertThat(xml, containsString(getMockIbanEntity().getIban()));
  }

  @Test
  void checkSchedulerAction_NoPa() throws StorageException, InterruptedException {

    when(paRepository.findByIdDominioIn(any(List.class))).thenReturn(Optional.empty());
    when(azureStorageInteraction.getUpdatedEC(anyString())).thenReturn(Map.of());

    // Funzione schedulata chiamata staticamente
    try {
      schedulerIca.updateIcaFile();
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }
}
