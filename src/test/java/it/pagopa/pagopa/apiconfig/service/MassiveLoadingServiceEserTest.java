package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.cosmos.repository.CdiCosmosRepository;
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

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockPaStazionePa;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockStazioni;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class, properties = {"properties.environment=PROD"})
class MassiveLoadingServiceEserTest {

    @MockBean
    private PaRepository paRepository;

    @MockBean
    private StazioniRepository stazioniRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @MockBean
    private CdiCosmosRepository cdiCosmosRepository;

    @Autowired
    @InjectMocks
    private MassiveLoadingService massiveLoadingService;


    @Test
    void manageCIStationRelationship() throws IOException {
        File csv = TestUtil.readFile("file/ci_station_ok.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

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
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

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
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        try {
            massiveLoadingService.manageCIStation(multipartFile);
        } catch (AppException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void massiveMigration() throws IOException {
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), any())).thenReturn(Optional.of(getMockPaStazionePa()), Optional.empty());
        when(paStazionePaRepository.save(any(PaStazionePa.class))).thenReturn(getMockPaStazionePa());

        File csv = TestUtil.readFile("file/massive_migration.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));
        massiveLoadingService.massiveMigration(multipartFile);

        verify(paStazionePaRepository, times(1))
                .save(any(PaStazionePa.class));

    }

    // broadcast enum valid
    @Test
    void massiveMigrationBadRequest_1() throws IOException {
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), any())).thenReturn(
                Optional.of(getMockPaStazionePa()),
                Optional.empty()
        );
        when(paStazionePaRepository.save(any(PaStazionePa.class))).thenReturn(getMockPaStazionePa());

        File csv = TestUtil.readFile("file/massive_migration_bad.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));
        AppException thrown = assertThrows(AppException.class,
                () -> {
                    massiveLoadingService.massiveMigration(multipartFile);
                });
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());

        verify(paStazionePaRepository, times(0))
                .save(any(PaStazionePa.class));
    }

    // broadcast consistent with station version
    @Test
    void massiveMigrationBadRequest_2() throws IOException {
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), any())).thenReturn(
                Optional.of(getMockPaStazionePa()),
                Optional.empty()
        );
        when(paStazionePaRepository.save(any(PaStazionePa.class))).thenReturn(getMockPaStazionePa());

        File csv = TestUtil.readFile("file/massive_migration_2.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));
        AppException thrown = assertThrows(AppException.class,
                () -> {
                    massiveLoadingService.massiveMigration(multipartFile);
                });
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getHttpStatus());

        verify(paStazionePaRepository, times(0))
                .save(any(PaStazionePa.class));
    }

    // start station not found
    @Test
    void massiveMigrationBadRequest_3() throws IOException {
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), any())).thenReturn(
                Optional.empty(),
                Optional.of(getMockPaStazionePa())
        );
        when(paStazionePaRepository.save(any(PaStazionePa.class))).thenReturn(getMockPaStazionePa());

        File csv = TestUtil.readFile("file/massive_migration_2.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));
        AppException thrown = assertThrows(AppException.class,
                () -> {
                    massiveLoadingService.massiveMigration(multipartFile);
                });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getHttpStatus());

        verify(paStazionePaRepository, times(0))
                .save(any(PaStazionePa.class));
    }

    @Test
    void massiveMigrationConflict_1() throws IOException {
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        PaStazionePa mockPaStazione = getMockPaStazionePa();
        when(paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(anyLong(), any())).thenReturn(
                Optional.of(mockPaStazione),
                Optional.of(mockPaStazione)
        );
        when(paStazionePaRepository.save(any(PaStazionePa.class))).thenReturn(getMockPaStazionePa());

        File csv = TestUtil.readFile("file/massive_migration_conflict.csv");
        MockMultipartFile multipartFile = new MockMultipartFile("file", csv.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(csv));
        AppException thrown = assertThrows(AppException.class,
                () -> {
                    massiveLoadingService.massiveMigration(multipartFile);
                });
        assertEquals(HttpStatus.CONFLICT, thrown.getHttpStatus());

        verify(paStazionePaRepository, times(0))
                .save(any(PaStazionePa.class));

    }
}
