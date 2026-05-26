package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.repository.IbanMasterSearchRepository;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanServiceProcessMassiveIbanOperationByCsvTest {

    public static final String IBAN_1 = "VALIDIBANII0000000000000001";
    public static final String IBAN_2 = "VALIDIBANII0000000000000002";
    public static final String IBAN_3 = "VALIDIBANII0000000000000003";
    public static final String EC_FISCAL_CODE = "11111111111";
    @Mock
    private PaRepository paRepository;
    @Mock
    private IbanRepository ibanRepository;
    @Mock
    private IbanMasterSearchRepository ibanMasterSearchRepository;
    @Mock
    private IbanAttributeRepository ibanAttributeRepository;
    @Mock
    private IbanAttributeMasterRepository ibanAttributeMasterRepository;
    @Mock
    private CodifichePaRepository codifichePaRepository;
    @Mock
    private EncodingsService encodingsService;
    @Mock
    private AzureStorageInteraction azureStorageInteraction;

    private IbanService ibanService;

    @BeforeEach
    void setup() {
        ibanService = new IbanService(
                "07601",
                "CUP",
                "ACA",
                100,
                1024 * 1024,
                paRepository,
                ibanRepository,
                ibanMasterSearchRepository,
                ibanAttributeRepository,
                ibanAttributeMasterRepository,
                codifichePaRepository,
                encodingsService,
                new ModelMapper(),
                azureStorageInteraction
        );
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_OK() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/all_operation_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId())).thenReturn(new ArrayList<>());
        when(ibanRepository.findByIban(IBAN_1)).thenReturn(Optional.empty());

        Iban existingUpdateIban = buildIban(20L, IBAN_2, EC_FISCAL_CODE);
        IbanMaster updateAssociation = buildIbanMaster(201L, pa, existingUpdateIban.getObjId(), futureValidityDate(), Collections.emptyList());
        existingUpdateIban.setIbanMasters(List.of(updateAssociation));

        when(ibanRepository.findByIban(IBAN_2)).thenReturn(Optional.of(existingUpdateIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingUpdateIban.getObjId(), pa.getObjId())).thenReturn(List.of(updateAssociation));

        Iban existingDeleteIban = buildIban(30L, IBAN_3, EC_FISCAL_CODE);
        IbanAttributeMaster ibanAttributeMaster = buildIbanAttributeMaster(500L);
        IbanMaster deleteAssociation = buildIbanMaster(301L, pa, existingDeleteIban.getObjId(), futureValidityDate(), List.of(ibanAttributeMaster));
        existingDeleteIban.setIbanMasters(List.of(deleteAssociation));

        when(ibanRepository.findByIban(IBAN_3)).thenReturn(Optional.of(existingDeleteIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingDeleteIban.getObjId(), pa.getObjId())).thenReturn(List.of(deleteAssociation));

        when(ibanRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(ibanMasterSearchRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> ibanService.processMassiveIbanOperationByCsv(file));

        verify(ibanRepository, times(2)).saveAll(anyList());
        verify(ibanMasterSearchRepository, atLeastOnce()).saveAll(anyList());
        verify(ibanAttributeMasterRepository).deleteByIds(anyList());
        verify(ibanMasterSearchRepository).deleteByIds(anyList());
        verify(ibanRepository).deleteByIds(anyList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing_required_header_ko.csv", "missing_required_field_value_ko.csv"})
    @SneakyThrows
    void processMassiveIbanOperationByCsv_error_missingRequiredHeader(String fileName) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("CSV not valid: either missing/invalid header or missing required field value"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_duplicateIbanInFile() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/duplicate_iban_ko.csv");


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Multiple operation on the same IBAN are not allowed"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"insert_with_empty_activation_date_ko.csv", "insert_with_null_activation_date_ko.csv"})
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_insertWithoutActivationDate(String fileName) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Missing required field 'dataattivazioneiban'"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_error_deleteWithUnexpectedFields() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/delete_unexpected_fields_ko.csv");


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Unexpected fields provided for delete operation"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_error_updateWithActivationDate() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/update_unexpected_activation_date_ko.csv");

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Unexpected field 'dataattivazioneiban' provided for update operation"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_error_updateWithoutUpdatableFields() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/update_no_updatable_field_provided_ko.csv");


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("No updatable fields provided for update operation"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_error_invalidOperationValue() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/not_existing_operation_ko.csv");


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("for column operazione. Allowed values: I (insert), D/C (delete), U/M (update)"));
    }

    private MultipartFile loadCsvFile(String path) throws IOException {
        File csv = TestUtil.readFile(path);
        String csvContent = Files.readString(csv.toPath());

        return new MockMultipartFile(
                "file",
                csv.getName(),
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                csvContent.getBytes(StandardCharsets.UTF_8)
        );
    }

    private Pa buildPa(Long objId, String creditorInstitutionCode) {
        return Pa.builder()
                .objId(objId)
                .idDominio(creditorInstitutionCode)
                .ragioneSociale("PA")
                .enabled(true)
                .build();
    }

    private Iban buildIban(Long objId, String ibanValue, String fiscalCode) {
        return Iban.builder()
                .objId(objId)
                .iban(ibanValue)
                .fiscalCode(fiscalCode)
                .dueDate(Timestamp.valueOf(LocalDateTime.now().plusYears(2)))
                .build();
    }

    private IbanMaster buildIbanMaster(
            Long objId,
            Pa pa,
            Long fkIban,
            Timestamp validityDate,
            List<IbanAttributeMaster> attributes
    ) {
        IbanMaster ibanMaster = IbanMaster.builder()
                .objId(objId)
                .fkPa(pa.getObjId())
                .fkIban(fkIban)
                .ibanStatus(IbanMaster.IbanStatus.ENABLED)
                .validityDate(validityDate)
                .insertedDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        ibanMaster.setIbanAttributesMasters(new ArrayList<>(attributes));
        return ibanMaster;
    }

    private IbanAttributeMaster buildIbanAttributeMaster(Long objId) {
        return IbanAttributeMaster.builder().objId(objId).build();
    }

    private Timestamp futureValidityDate() {
        return Timestamp.valueOf(LocalDateTime.now().plusYears(5));
    }
}

