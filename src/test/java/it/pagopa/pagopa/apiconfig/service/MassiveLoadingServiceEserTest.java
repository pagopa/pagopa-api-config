package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class, properties = {"properties.environment=PROD"})
class MassiveLoadingServiceEserTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private StazioniRepository stazioniRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    @InjectMocks
    private MassiveLoadingService massiveLoadingService;


    @Test
    void manageCIStationRelationship() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void manageCIStationRelationship_ko_1() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.empty());
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.empty());
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void manageCIStationRelationship_ko_2() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.of(getMockPaStazionePa()));

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void manageCIStationRelationship_ko_3() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.empty());
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void manageCIStationRelationship_ko_4() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ko_1.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), anyLong())).thenReturn(Optional.empty());

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail(e);
        }
    }
}
