package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.starter.entity.CdsCategoria;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggetto;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import it.gov.pagopa.apiconfig.starter.repository.CdsSoggettoRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@org.springframework.stereotype.Service
@Validated
@Transactional
public class CdsService {

  @Autowired private CdsServizioRepository cdsServizioRepository;
  @Autowired private CdsSoggettoRepository cdsSoggettoRepository;
  @Autowired private PaRepository paRepository;

  @Transactional(readOnly = true)
  public List<CdsServizio> getCdsServices() {
    return cdsServizioRepository.findAllFetching();
  }

  @Transactional(readOnly = true)
  public CdsServizio getCdsService(String idServizio) {
    return findCdsServizio(idServizio);
  }

  public CdsServizio createCdsService(CdsServizio cdsServizio) {
    requireIdServizio(cdsServizio.getIdServizio());
    if (findCdsServizioOptional(cdsServizio.getIdServizio()).isPresent()) {
      throw new AppException(AppError.CDS_SERVIZIO_CONFLICT, cdsServizio.getIdServizio());
    }

    CdsServizio entity =
        CdsServizio.builder()
            .idServizio(cdsServizio.getIdServizio())
            .descrizioneServizio(cdsServizio.getDescrizioneServizio())
            .xsdRiferimento(cdsServizio.getXsdRiferimento())
            .version(cdsServizio.getVersion())
            .categoriaId(cdsServizio.getCategoriaId())
            .categoria(resolveCategoria(cdsServizio))
            .build();
    return cdsServizioRepository.save(entity);
  }

  public CdsServizio updateCdsService(String idServizio, CdsServizio cdsServizio) {
    requireIdServizio(idServizio);
    CdsServizio existing = findCdsServizio(idServizio);

    existing.setIdServizio(idServizio);
    existing.setDescrizioneServizio(cdsServizio.getDescrizioneServizio());
    existing.setXsdRiferimento(cdsServizio.getXsdRiferimento());
    existing.setVersion(cdsServizio.getVersion());
    existing.setCategoriaId(cdsServizio.getCategoriaId());
    existing.setCategoria(resolveCategoria(cdsServizio));

    return cdsServizioRepository.save(existing);
  }

  public void deleteCdsService(String idServizio) {
    CdsServizio existing = findCdsServizio(idServizio);
    cdsServizioRepository.delete(existing);
  }

  @Transactional(readOnly = true)
  public List<CdsSoggetto> getCdsSubjects() {
    return cdsSoggettoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public CdsSoggetto getCdsSubject(Long idSoggetto) {
    return findCdsSubject(idSoggetto);
  }

  public CdsSoggetto createCdsSubject(CdsSoggetto cdsSoggetto) {
    if (cdsSoggetto.getId() != null && findCdsSubjectOptional(cdsSoggetto.getId()).isPresent()) {
      throw new AppException(AppError.CDS_SOGGETTO_CONFLICT, cdsSoggetto.getId());
    }
    validateCreditorInstitutionExists(cdsSoggetto.getCreditorInstitutionCode());

    CdsSoggetto entity =
        CdsSoggetto.builder()
            .id(cdsSoggetto.getId())
            .creditorInstitutionCode(cdsSoggetto.getCreditorInstitutionCode())
            .creditorInstitutionDescription(cdsSoggetto.getCreditorInstitutionDescription())
            .build();
    return cdsSoggettoRepository.save(entity);
  }

  public CdsSoggetto updateCdsSubject(Long idSoggetto, CdsSoggetto cdsSoggetto) {
    CdsSoggetto existing = findCdsSubject(idSoggetto);
    validateCreditorInstitutionExists(cdsSoggetto.getCreditorInstitutionCode());

    existing.setId(idSoggetto);
    existing.setCreditorInstitutionCode(cdsSoggetto.getCreditorInstitutionCode());
    existing.setCreditorInstitutionDescription(cdsSoggetto.getCreditorInstitutionDescription());

    return cdsSoggettoRepository.save(existing);
  }

  public void deleteCdsSubject(Long idSoggetto) {
    CdsSoggetto existing = findCdsSubject(idSoggetto);
    cdsSoggettoRepository.delete(existing);
  }

  private CdsServizio findCdsServizio(String idServizio) {
    return findCdsServizioOptional(idServizio)
        .orElseThrow(() -> new AppException(AppError.CDS_SERVIZIO_NOT_FOUND, idServizio));
  }

  private java.util.Optional<CdsServizio> findCdsServizioOptional(String idServizio) {
    return cdsServizioRepository.findAllFetching().stream()
        .filter(Objects::nonNull)
        .filter(elem -> Objects.equals(elem.getIdServizio(), idServizio))
        .findFirst();
  }

  private void requireIdServizio(String idServizio) {
    if (idServizio == null || idServizio.isBlank()) {
      throw new AppException(AppError.CDS_SERVIZIO_BAD_REQUEST);
    }
  }

  private CdsCategoria resolveCategoria(CdsServizio cdsServizio) {
    if (cdsServizio.getCategoria() != null) {
      return cdsServizio.getCategoria();
    }

    if (cdsServizio.getCategoriaId() == null) {
      return null;
    }

    return CdsCategoria.builder().id(cdsServizio.getCategoriaId()).build();
  }

  private CdsSoggetto findCdsSubject(Long idSoggetto) {
    return findCdsSubjectOptional(idSoggetto)
        .orElseThrow(() -> new AppException(AppError.CDS_SOGGETTO_NOT_FOUND, idSoggetto));
  }

  private Optional<CdsSoggetto> findCdsSubjectOptional(Long idSoggetto) {
    return cdsSoggettoRepository.findAll().stream()
        .filter(Objects::nonNull)
        .filter(elem -> Objects.equals(elem.getId(), idSoggetto))
        .findFirst();
  }

  private void validateCreditorInstitutionExists(String creditorInstitutionCode) {
    paRepository
        .findByIdDominio(creditorInstitutionCode)
        .orElseThrow(
            () ->
                new AppException(
                    AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
  }
}
