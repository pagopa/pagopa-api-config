package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ApiConfig.class)
class PaStazionePaRepositoryTest {

    @Autowired
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private StazioniRepository stazioniRepository;

    @Autowired
    private IntermediariPaRepository intermediariPaRepository;

    @Test
    void findByFields_1() {
        Pa pa = TestUtil.getMockPa();
        paRepository.save(pa);

        IntermediariPa intermediariPa = TestUtil.getMockIntermediariePa();
        intermediariPaRepository.save(intermediariPa);

        Stazioni stazioni = TestUtil.getMockStazioni();
        stazioni.setObjId(1L);
        stazioniRepository.save(stazioni);

        PaStazionePa paStazionePa = TestUtil.getMockPaStazionePa();
        paStazionePa.setFkStazione(stazioni);
        paStazionePa.setSegregazione(1L);
        paStazionePa.setProgressivo(1L);
        paStazionePaRepository.save(paStazionePa);

        assertTrue(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                1L, 1L, 1L, false, 1L, 1L).isPresent());
    }

    @Test
    void findByFields_2() {
        Pa pa = TestUtil.getMockPa();
        paRepository.save(pa);

        IntermediariPa intermediariPa = TestUtil.getMockIntermediariePa();
        intermediariPaRepository.save(intermediariPa);

        Stazioni stazioni = TestUtil.getMockStazioni();
        stazioni.setObjId(1L);
        stazioniRepository.save(stazioni);

        PaStazionePa paStazionePa = TestUtil.getMockPaStazionePa();
        paStazionePa.setFkStazione(stazioni);
        paStazionePa.setAuxDigit(0L);
        paStazionePa.setSegregazione(1L);
        paStazionePa.setProgressivo(1L);
        paStazionePaRepository.save(paStazionePa);

        assertTrue(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                1L, 1L, 0L, false, 1L, 1L).isPresent());
    }

    @Test
    void findByFields_3() {
        Pa pa = TestUtil.getMockPa();
        paRepository.save(pa);

        IntermediariPa intermediariPa = TestUtil.getMockIntermediariePa();
        intermediariPaRepository.save(intermediariPa);

        Stazioni stazioni = TestUtil.getMockStazioni();
        stazioni.setObjId(1L);
        stazioniRepository.save(stazioni);

        PaStazionePa paStazionePa = TestUtil.getMockPaStazionePa();
        paStazionePa.setFkStazione(stazioni);
        paStazionePa.setAuxDigit(3L);
        paStazionePa.setSegregazione(1L);
        paStazionePa.setProgressivo(1L);
        paStazionePaRepository.save(paStazionePa);

        assertTrue(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                1L, 1L, 3L, false, 1L, 1L).isPresent());
    }

    @Test
    void findByFields_4() {
        Pa pa = TestUtil.getMockPa();
        paRepository.save(pa);

        IntermediariPa intermediariPa = TestUtil.getMockIntermediariePa();
        intermediariPaRepository.save(intermediariPa);

        Stazioni stazioni = TestUtil.getMockStazioni();
        stazioni.setObjId(1L);
        stazioniRepository.save(stazioni);

        PaStazionePa paStazionePa = TestUtil.getMockPaStazionePa();
        paStazionePa.setFkStazione(stazioni);
        paStazionePa.setAuxDigit(3L);
        paStazionePa.setSegregazione(null);
        paStazionePa.setProgressivo(null);
        paStazionePaRepository.save(paStazionePa);

        assertTrue(paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                1L, 1L, 3L, false, null, null).isPresent());
    }

    @Test
    void search() {
        Specification<PaStazionePa> specification = PaStazionePaRepository.search(1L, 1L, 1L, false, 1L, 1L);
        assertNotNull(specification);
    }
}
