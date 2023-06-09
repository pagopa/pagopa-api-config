package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.creditorinstitution.Icas;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.InformativeContoAccreditoMaster;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IcaBinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import org.assertj.core.util.Lists;
import org.checkerframework.dataflow.qual.TerminatesExecution;
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
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.getMockBrokerDetails;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIban;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanAttributeMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanEntity;
import static it.gov.pagopa.apiconfig.TestUtil.getMockIbanMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockInformativeContoAccreditoMaster;
import static it.gov.pagopa.apiconfig.TestUtil.getMockPa;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
public class IbanServiceTest {


  @MockBean private PaRepository paRepository;

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private IbanAttributeRepository ibanAttributeRepository;

  @MockBean private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @MockBean private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired @InjectMocks private IbanService ibanService;

  @Test
  void deleteIban() throws JSONException, IOException {
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.of(getMockIbanEntity()));
    when(paRepository.findByIdDominio(any())).thenReturn(Optional.of(getMockPa()));
    when(ibanMasterRepository.findByFkIbanAndFkPa(any(), any())).thenReturn(List.of(getMockIbanMaster()));
    when(ibanAttributeMasterRepository.findByFkIbanMasterIn(any())).thenReturn(List.of(getMockIbanAttributeMaster()));

    ibanService.deleteIban("1234", "IT99C0222211111000000000000");
    assertTrue(true);
  }

  @Test
  void deleteIban_NotFound(){
    when(ibanRepository.findByIban(anyString())).thenReturn(Optional.empty());
    try {
      ibanService.deleteIban("1234", "IT99C0222211111000000000000");
    } catch (AppException e) {
      assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }
}
