package it.gov.pagopa.apiconfig.core.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.service.MassiveLoadingService;
import java.io.File;
import java.io.FileInputStream;
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
class MassiveLoadingControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private MassiveLoadingService massiveLoadingService;

  @Test
  void manageCIStationRelationship() throws Exception {
    File csv = TestUtil.readFile("file/ci_station_ok.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    String url = "/batchoperation/creditorinstitution-station/loading";
    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }

  @Test
  void manageCIStationRelationship_400() throws Exception {
    File csv = TestUtil.readFile("file/ci_station_ko_1.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    AppException exception =
        new AppException(AppError.MASSIVELOADING_BAD_REQUEST, "MASSIVELOADING_BAD_REQUEST");
    doThrow(exception).when(massiveLoadingService).manageCIStation(multipartFile);

    String url = "/batchoperation/creditorinstitution-station/loading";
    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isBadRequest());
  }

  @Test
  void massiveMigration() throws Exception {
    File csv = TestUtil.readFile("file/massive_migration.csv");
    MockMultipartFile multipartFile =
        new MockMultipartFile(
            "file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

    String url = "/batchoperation/creditorinstitution-station/migration";
    mvc.perform(multipart(url).file(multipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }
}
