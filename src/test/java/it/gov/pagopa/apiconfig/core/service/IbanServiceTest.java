package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IcaBinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ApiConfig.class)
public class IbanServiceTest {


  @MockBean private PaRepository paRepository;

  @MockBean private IbanRepository ibanRepository;

  @MockBean private IbanMasterRepository ibanMasterRepository;

  @MockBean private IbanAttributeRepository ibanAttributeRepository;

  @MockBean private IbanAttributeMasterRepository ibanAttributeMasterRepository;

  @MockBean private IcaBinaryFileRepository icaBinaryFileRepository;

  @MockBean private IbanService ibanService;

}
