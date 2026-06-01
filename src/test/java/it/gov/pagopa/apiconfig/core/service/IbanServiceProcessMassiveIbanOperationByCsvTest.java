package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.repository.ExtendedCodifichePaRepository;
import it.gov.pagopa.apiconfig.core.repository.IbanMasterSearchRepository;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanAttributeMaster;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanAttributeRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IbanServiceProcessMassiveIbanOperationByCsvTest {

    public static final String IBAN_1 = "IT84H0706676470000000822789";
    public static final String IBAN_2 = "IT74L0306905020100000046450";
    public static final String IBAN_3 = "IT04I0103061821000000248378";
    public static final String POSTAL_IBAN = "IT59A0760112000000080969991";
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
    private ExtendedCodifichePaRepository codifichePaRepository;
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
        IbanMaster updateAssociation = buildIbanMaster(201L, pa, existingUpdateIban.getObjId(), Collections.emptyList());
        existingUpdateIban.setIbanMasters(List.of(updateAssociation));

        when(ibanRepository.findByIban(IBAN_2)).thenReturn(Optional.of(existingUpdateIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingUpdateIban.getObjId(), pa.getObjId())).thenReturn(List.of(updateAssociation));

        Iban existingDeleteIban = buildIban(30L, IBAN_3, EC_FISCAL_CODE);
        IbanAttributeMaster ibanAttributeMaster = buildIbanAttributeMaster();
        IbanMaster deleteAssociation = buildIbanMaster(301L, pa, existingDeleteIban.getObjId(), List.of(ibanAttributeMaster));
        existingDeleteIban.setIbanMasters(List.of(deleteAssociation));

        when(ibanRepository.findByIban(IBAN_3)).thenReturn(Optional.of(existingDeleteIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingDeleteIban.getObjId(), pa.getObjId())).thenReturn(List.of(deleteAssociation));

        assertDoesNotThrow(() -> ibanService.processMassiveIbanOperationByCsv(file));

        verify(ibanRepository, times(2)).saveAll(anyList());
        verify(ibanMasterSearchRepository).saveAll(anyList());
        verify(ibanAttributeMasterRepository).deleteByIds(anyList());
        verify(ibanMasterSearchRepository).deleteByIds(anyList());
        verify(ibanRepository).deleteByIds(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_OK_insertPostalIban() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/insert_postal_iban_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId())).thenReturn(new ArrayList<>());
        when(ibanRepository.findByIban(POSTAL_IBAN)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> ibanService.processMassiveIbanOperationByCsv(file));

        verify(ibanRepository).saveAll(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_OK_updatePostalIban() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/update_postal_iban_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        Iban existingIban = buildIban(20L, POSTAL_IBAN, EC_FISCAL_CODE);
        IbanMaster ibanMaster = buildIbanMaster(201L, pa, existingIban.getObjId(), Collections.emptyList());
        existingIban.setIbanMasters(List.of(ibanMaster));

        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId())).thenReturn(new ArrayList<>());
        when(ibanRepository.findByIban(POSTAL_IBAN)).thenReturn(Optional.of(existingIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingIban.getObjId(), pa.getObjId())).thenReturn(List.of(ibanMaster));

        assertDoesNotThrow(() -> ibanService.processMassiveIbanOperationByCsv(file));

        verify(ibanRepository).saveAll(anyList());
        verify(ibanMasterSearchRepository).saveAll(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_OK_deletePostalIban() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/delete_postal_iban_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        Iban existingIban = buildIban(20L, POSTAL_IBAN, EC_FISCAL_CODE);
        IbanAttributeMaster ibanAttributeMaster = buildIbanAttributeMaster();
        IbanMaster ibanMaster = buildIbanMaster(201L, pa, existingIban.getObjId(), List.of(ibanAttributeMaster));
        existingIban.setIbanMasters(List.of(ibanMaster));

        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(ibanRepository.findByIban(POSTAL_IBAN)).thenReturn(Optional.of(existingIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingIban.getObjId(), pa.getObjId())).thenReturn(List.of(ibanMaster));
        when(codifichePaRepository.findByCodicePaAndFkPa_ObjId(anyString(), anyLong()))
                .thenReturn(Optional.of(CodifichePa.builder().id(98L).build()));

        assertDoesNotThrow(() -> ibanService.processMassiveIbanOperationByCsv(file));

        verify(ibanRepository).deleteByIds(anyList());
        verify(ibanMasterSearchRepository).deleteByIds(anyList());
        verify(ibanAttributeMasterRepository).deleteByIds(anyList());
        verify(codifichePaRepository).deleteByIds(anyList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"missing_required_header_ko.csv", "missing_required_field_value_ko.csv"})
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_missingRequiredHeader(String fileName) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("CSV not valid: either missing/invalid header or missing required field value"));
    }

    @ParameterizedTest
    @CsvSource({
            "duplicate_iban_ko.csv, Multiple operation on the same IBAN are not allowed",
            "update_unexpected_activation_date_ko.csv, Unexpected field 'dataattivazioneiban' provided for update operation",
            "update_no_updatable_field_provided_ko.csv, No updatable fields provided for update operation",
            "not_existing_operation_ko.csv, for column operazione. Allowed values: I (insert), D/C (delete), U/M (update)",
    })
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_badRequest(String fileName, String expectedErrMsg) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains(expectedErrMsg));
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

    @ParameterizedTest
    @ValueSource(strings = {
            "delete_unexpected_activation_date_ko.csv",
            "delete_unexpected_description_ko.csv",
            "delete_unexpected_due_date_ko.csv"
    })
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_deleteWithUnexpectedFields(String fileName) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);


        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("Unexpected fields provided for delete operation"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "insert_invalid_iban.csv",
            "update_invalid_iban.csv",
            "delete_invalid_iban.csv"
    })
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_invalidIbanValue(String fileName) {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/" + fileName);

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("The provided IBAN is invalid:"));
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_updateFailsWithNotFoundAfterInsert_deleteNotExecuted() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/all_operation_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId())).thenReturn(new ArrayList<>());

        when(ibanRepository.findByIban(IBAN_1)).thenReturn(Optional.empty());

        Iban existingUpdateIban = buildIban(20L, IBAN_2, EC_FISCAL_CODE);
        existingUpdateIban.setIbanMasters(Collections.emptyList());
        when(ibanRepository.findByIban(IBAN_2)).thenReturn(Optional.of(existingUpdateIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingUpdateIban.getObjId(), pa.getObjId()))
                .thenReturn(Collections.emptyList());

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());

        verify(ibanRepository, times(1)).saveAll(anyList());
        verify(ibanAttributeMasterRepository, never()).deleteByIds(anyList());
        verify(ibanMasterSearchRepository, never()).deleteByIds(anyList());
        verify(ibanRepository, never()).deleteByIds(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_insertIbanAlreadyAssociated() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/insert_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        Iban existingIban = buildIban(20L, IBAN_1, EC_FISCAL_CODE);
        IbanMaster ibanMaster = buildIbanMaster(201L, pa, existingIban.getObjId(), Collections.emptyList());
        existingIban.setIbanMasters(List.of(ibanMaster));

        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(ibanRepository.findByIban(IBAN_1)).thenReturn(Optional.of(existingIban));

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("already associated to the creditor institution"));

        verify(ibanRepository, never()).saveAll(anyList());
        verify(ibanMasterSearchRepository, never()).saveAll(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_insertAlreadyAssociatedPostalIban() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/insert_postal_iban_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        Pa paPostalIban = buildPa(2L, "234513");
        Iban existingIban = buildIban(20L, POSTAL_IBAN, "234513");
        IbanMaster ibanMaster = buildIbanMaster(201L, paPostalIban, existingIban.getObjId(), Collections.emptyList());
        existingIban.setIbanMasters(List.of(ibanMaster));

        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(ibanRepository.findByIban(POSTAL_IBAN)).thenReturn(Optional.of(existingIban));

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("already associated to one CI, this type of IBAN cannot be"));

        verify(ibanRepository, never()).saveAll(anyList());
    }

    @Test
    @SneakyThrows
    void processMassiveIbanOperationByCsv_KO_updateAlreadyAssociatedPostalIban() {
        MultipartFile file = loadCsvFile("file/massiveIbanOperationByCsv/update_postal_iban_ok.csv");

        Pa pa = buildPa(1L, EC_FISCAL_CODE);
        Pa paPostalIban = buildPa(2L, "234513");
        Iban existingIban = buildIban(20L, POSTAL_IBAN, "234513");
        IbanMaster ibanMaster = buildIbanMaster(201L, paPostalIban, existingIban.getObjId(), Collections.emptyList());
        existingIban.setIbanMasters(List.of(ibanMaster));

        when(paRepository.findByIdDominio(EC_FISCAL_CODE)).thenReturn(Optional.of(pa));
        when(ibanRepository.findByIban(POSTAL_IBAN)).thenReturn(Optional.of(existingIban));
        when(ibanMasterSearchRepository.findByFkIbanAndFkPa(existingIban.getObjId(), pa.getObjId())).thenReturn(List.of(ibanMaster));

        AppException ex = assertThrows(
                AppException.class,
                () -> ibanService.processMassiveIbanOperationByCsv(file)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
        assertTrue(ex.getMessage().contains("already associated to one CI, this type of IBAN cannot be"));

        verify(ibanRepository, never()).saveAll(anyList());
        verify(ibanMasterSearchRepository, never()).saveAll(anyList());
    }

    private MultipartFile loadCsvFile(String path) throws IOException {
        File csv = TestUtil.readFile(path);
        String csvContent = Files.readString(csv.toPath());

        return new MockMultipartFile(
                "file",
                csv.getName(),
                "text/csv",
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
            List<IbanAttributeMaster> attributes
    ) {
        IbanMaster ibanMaster = IbanMaster.builder()
                .objId(objId)
                .fkPa(pa.getObjId())
                .fkIban(fkIban)
                .ibanStatus(IbanMaster.IbanStatus.ENABLED)
                .validityDate(futureValidityDate())
                .insertedDate(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        ibanMaster.setIbanAttributesMasters(new ArrayList<>(attributes));
        return ibanMaster;
    }

    private IbanAttributeMaster buildIbanAttributeMaster() {
        return IbanAttributeMaster.builder().objId(500L).build();
    }

    private Timestamp futureValidityDate() {
        return Timestamp.valueOf(LocalDateTime.now().plusYears(5));
    }
}
