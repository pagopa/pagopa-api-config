package it.pagopa.pagopa.apiconfig.util;



import com.opencsv.exceptions.CsvConstraintViolationException;
import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.model.massiveloading.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CreditorInstitutionStationVerifierTest {
    @MockBean
    private PaRepository paRepository;

    @MockBean
    private StazioniRepository stazioniRepository;

    @MockBean
    private PaStazionePaRepository paStazionePaRepository;

    @ParameterizedTest
    @ValueSource(strings = {
            "UAT",
            "PROD",
    })
    void passed(String env) throws CsvConstraintViolationException {
        CreditorInstitutionStationVerifier creditorInstitutionStationVerifier =
                new CreditorInstitutionStationVerifier(env, paRepository, stazioniRepository, paStazionePaRepository);

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        CreditorInstitutionStation creditorInstitutionStation =
                CreditorInstitutionStation.builder()
                        .creditorInstitutionId("ec")
                        .stationId("station")
                        .environment(env.equals("PROD") ? CreditorInstitutionStation.Env.ESER : CreditorInstitutionStation.Env.COLL)
                        .broadcast(CreditorInstitutionStation.YesNo.N)
                        .auxDigit(3)
                        .segregationCode("47")
                        .applicationCode(null)
                        .operation(CreditorInstitutionStation.Operation.A)
                        .build();

        assertTrue(creditorInstitutionStationVerifier.verifyBean(creditorInstitutionStation));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UAT",
            "PROD",
    })
    void failed(String env) throws CsvConstraintViolationException {
        CreditorInstitutionStationVerifier creditorInstitutionStationVerifier =
                new CreditorInstitutionStationVerifier(env, paRepository, stazioniRepository, paStazionePaRepository);

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        CreditorInstitutionStation creditorInstitutionStation =
                CreditorInstitutionStation.builder()
                        .creditorInstitutionId("ec")
                        .stationId("station")
                        .environment(env.equals("PROD") ? CreditorInstitutionStation.Env.COLL : CreditorInstitutionStation.Env.ESER)
                        .broadcast(CreditorInstitutionStation.YesNo.N)
                        .auxDigit(3)
                        .segregationCode("47")
                        .applicationCode(null)
                        .operation(CreditorInstitutionStation.Operation.A)
                        .build();
        assertFalse(creditorInstitutionStationVerifier.verifyBean(creditorInstitutionStation));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UAT",
            "PROD",
    })
    void exception_1(String env) {
        CreditorInstitutionStationVerifier creditorInstitutionStationVerifier =
                new CreditorInstitutionStationVerifier(env, paRepository, stazioniRepository, paStazionePaRepository);

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.empty());
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.empty());
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        CreditorInstitutionStation creditorInstitutionStation =
                CreditorInstitutionStation.builder()
                        .creditorInstitutionId("ec")
                        .stationId("station")
                        .environment(env.equals("PROD") ? CreditorInstitutionStation.Env.COLL : CreditorInstitutionStation.Env.ESER)
                        .broadcast(CreditorInstitutionStation.YesNo.N)
                        .auxDigit(4)
                        .segregationCode("47")
                        .applicationCode(null)
                        .operation(CreditorInstitutionStation.Operation.A)
                        .build();
        try {
            creditorInstitutionStationVerifier.verifyBean(creditorInstitutionStation);
        } catch (CsvConstraintViolationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UAT",
            "PROD",
    })
    void exception_2(String env) {
        CreditorInstitutionStationVerifier creditorInstitutionStationVerifier =
                new CreditorInstitutionStationVerifier(env, paRepository, stazioniRepository, paStazionePaRepository);

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.of(getMockPaStazionePa()));

        CreditorInstitutionStation creditorInstitutionStation =
                CreditorInstitutionStation.builder()
                        .creditorInstitutionId("ec")
                        .stationId("station")
                        .environment(env.equals("PROD") ? CreditorInstitutionStation.Env.COLL : CreditorInstitutionStation.Env.ESER)
                        .broadcast(CreditorInstitutionStation.YesNo.N)
                        .auxDigit(3)
                        .segregationCode("4")
                        .applicationCode("1")
                        .operation(CreditorInstitutionStation.Operation.A)
                        .build();
        try {
            creditorInstitutionStationVerifier.verifyBean(creditorInstitutionStation);
        } catch (CsvConstraintViolationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "UAT",
            "PROD",
    })
    void exception_3(String env) {
        CreditorInstitutionStationVerifier creditorInstitutionStationVerifier =
                new CreditorInstitutionStationVerifier(env, paRepository, stazioniRepository, paStazionePaRepository);

        when(paRepository.findByIdDominio(anyString())).thenReturn(Optional.of(getMockPa()));
        when(stazioniRepository.findByIdStazione(anyString())).thenReturn(Optional.of(getMockStazioni()));
        when(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                anyLong(), anyLong(), anyLong(), anyBoolean(), anyLong(), anyLong()))
                .thenReturn(Optional.empty());

        CreditorInstitutionStation creditorInstitutionStation =
                CreditorInstitutionStation.builder()
                        .creditorInstitutionId("ec")
                        .stationId("station")
                        .environment(env.equals("PROD") ? CreditorInstitutionStation.Env.COLL : CreditorInstitutionStation.Env.ESER)
                        .broadcast(CreditorInstitutionStation.YesNo.N)
                        .auxDigit(3)
                        .segregationCode("4")
                        .applicationCode("1")
                        .operation(CreditorInstitutionStation.Operation.C)
                        .build();
        try {
            creditorInstitutionStationVerifier.verifyBean(creditorInstitutionStation);
        } catch (CsvConstraintViolationException e) {
            assertTrue(true);
        } catch (Exception e) {
            fail(e);
        }
    }

}
