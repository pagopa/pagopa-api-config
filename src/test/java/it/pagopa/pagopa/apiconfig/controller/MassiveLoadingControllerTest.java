package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.service.MassiveLoadingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
public class MassiveLoadingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MassiveLoadingService massiveLoadingService;

    @Test
    void manageCIStationRelationship() throws Exception {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        String url = "/massiveloading/creditorinstitution-station";
        mvc.perform(multipart(url)
                .file(multipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    void manageCIStationRelationship_400() throws Exception {
        File csv = TestUtil.readFile("file/ci_station_ko_1.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        AppException exception = new AppException(AppError.MASSIVELOADING_BAD_REQUEST, "MASSIVELOADING_BAD_REQUEST");
        doThrow(exception).when(massiveLoadingService).manageCIStation(multipartFile);

        String url = "/massiveloading/creditorinstitution-station";
        mvc.perform(multipart(url)
                .file(multipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

}
