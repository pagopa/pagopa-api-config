package it.gov.pagopa.apiconfig.core.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;

@SpringBootTest(classes = ApiConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaStazionePaRepositoryTest {

  @Autowired private PaStazionePaRepository paStazionePaRepository;

  @Autowired private PaRepository paRepository;

  @Autowired private StazioniRepository stazioniRepository;

  @Autowired private IntermediariPaRepository intermediariPaRepository;

  private PaStazionePa paStazionePa;
  private Pa ci;
  private Stazioni stazione;

  @BeforeAll
  void setup() {
    Pa pa = TestUtil.getMockPa();
    ci = paRepository.save(pa);

    IntermediariPa intermediariPa = TestUtil.getMockIntermediariePa();
    var inter = intermediariPaRepository.save(intermediariPa);

    Stazioni stazioni = TestUtil.getMockStazioni();
    stazioni.setObjId(1L);
    stazioni.setIntermediarioPa(inter);
    stazione = stazioniRepository.save(stazioni);

    PaStazionePa entity = TestUtil.getMockPaStazionePa();
    entity.setFkStazione(stazione);
    entity.setPa(ci);
    entity.setSegregazione(1L);
    entity.setProgressivo(1L);
    paStazionePa = paStazionePaRepository.save(entity);
  }

  @Test
  void findByFields_1() {
    assertTrue(
        paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                ci.getObjId(), stazione.getObjId(), 1L, false, 1L, 1L)
            .isPresent());
  }

  @Test
  void findByFields_2() {
    paStazionePa.setAuxDigit(0L);
    paStazionePaRepository.save(paStazionePa);
    assertTrue(
        paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                ci.getObjId(), stazione.getObjId(), 0L, false, 1L, 1L)
            .isPresent());
  }

  @Test
  void findByFields_3() {
    paStazionePa.setAuxDigit(3L);
    paStazionePaRepository.save(paStazionePa);
    assertTrue(
        paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                ci.getObjId(), stazione.getObjId(), 3L, false, 1L, 1L)
            .isPresent());
  }

  @Test
  void findByFields_4() {
    paStazionePa.setAuxDigit(3L);
    paStazionePa.setSegregazione(null);
    paStazionePa.setProgressivo(null);
    paStazionePaRepository.save(paStazionePa);
    assertTrue(
        paStazionePaRepository
            .findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                ci.getObjId(), stazione.getObjId(), 3L, false, null, null)
            .isPresent());
  }

  @Test
  void search() {
    Specification<PaStazionePa> specification =
        PaStazionePaRepository.search(1L, 1L, 1L, false, 1L, 1L);
    assertNotNull(specification);
  }
}
