package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.cds.CdsServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioList;
import it.gov.pagopa.apiconfig.core.model.cds.CdsSoggettoServizioRequestDto;
import it.gov.pagopa.apiconfig.starter.entity.*;
import it.gov.pagopa.apiconfig.starter.repository.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@org.springframework.stereotype.Service
@Validated
@Transactional
public class CdsService {

  @Autowired private CdsServizioRepository cdsServizioRepository;
  @Autowired private CdsSoggettoRepository cdsSoggettoRepository;
  @Autowired private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @Autowired private PaStazionePaRepository paStazionePaRepository;
  @Autowired private PaRepository paRepository;
  @Autowired private StazioniRepository stazioniRepository;
  @Autowired private EntityManager entityManager;

  @Transactional(readOnly = true)
  public CdsServizioList getCdsServices() {
    return CdsServizioList.builder().services(cdsServizioRepository.findAllFetching()).build();
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
  public CdsSoggettoList getCdsSubjects() {
    return CdsSoggettoList.builder().subjects(cdsSoggettoRepository.findAll()).build();
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

  @Transactional(readOnly = true)
  public CdsSoggettoServizioList getCdsSubjectServices(String idSoggetto) {
    String subjectObjectId = getSubjectObjectId(idSoggetto);
    List<CdsSoggettoServizio> subjectServices =
        cdsSoggettoServizioRepository.findAllFetching().stream()
            .filter(Objects::nonNull)
            .filter(elem -> Objects.equals(elem.getFkCdsSoggetto(), subjectObjectId))
            .map(this::toResponseCdsSoggettoServizio)
            .toList();
    return CdsSoggettoServizioList.builder().subjectServices(subjectServices).build();
  }

  @Transactional(readOnly = true)
  public CdsSoggettoServizio getCdsSubjectService(String idSoggetto, String idSoggettoServizio) {
    return toResponseCdsSoggettoServizio(findCdsSubjectService(idSoggetto, idSoggettoServizio));
  }

  public CdsSoggettoServizio createCdsSubjectService(
      String idSoggetto, CdsSoggettoServizioRequestDto cdsSoggettoServizioRequestDto) {
    CdsSoggetto soggetto = findCdsSubjectByCreditorInstitutionCode(idSoggetto);
    String subjectObjectId = requireSubjectObjectId(soggetto, idSoggetto);
    requireIdSoggettoServizio(cdsSoggettoServizioRequestDto.getId());
    requireIdServizio(cdsSoggettoServizioRequestDto.getIdServizio());
    CdsServizio servizio = findCdsServizio(cdsSoggettoServizioRequestDto.getIdServizio());

    if (findCdsSubjectServiceOptional(subjectObjectId, cdsSoggettoServizioRequestDto.getId())
        .isPresent()) {
      throw new AppException(
          AppError.CDS_SOGGETTO_SERVIZIO_CONFLICT,
          cdsSoggettoServizioRequestDto.getId(),
          idSoggetto);
    }

    String stationFk = resolveStationFk(cdsSoggettoServizioRequestDto.getIdStazione(), soggetto.getCreditorInstitutionCode());

    CdsSoggettoServizio entity =
        CdsSoggettoServizio.builder()
            .fkCdsSoggetto(subjectObjectId)
            .fkCdsServizio(String.valueOf(servizio.getId()))
            .fkStazione(stationFk)
            .idSoggettoServizio(cdsSoggettoServizioRequestDto.getId())
            .descrizioneServizio(cdsSoggettoServizioRequestDto.getDescrizioneServizio())
            .dataInizioValidita(cdsSoggettoServizioRequestDto.getDataInizioValidita())
            .dataFineValidita(cdsSoggettoServizioRequestDto.getDataFineValidita())
            .commissione(cdsSoggettoServizioRequestDto.getCommissione())
            .soggetto(soggetto)
            .servizio(servizio)
            .stazionePa(resolveStation(cdsSoggettoServizioRequestDto.getIdStazione()))
            .build();
    return toResponseCdsSoggettoServizio(saveAndRefresh(entity));
  }

  public CdsSoggettoServizio updateCdsSubjectService(
      String idSoggetto,
      String idSoggettoServizio,
      CdsSoggettoServizioRequestDto cdsSoggettoServizioRequestDto) {

    CdsSoggetto soggetto = findCdsSubjectByCreditorInstitutionCode(idSoggetto);
    String subjectObjectId = requireSubjectObjectId(soggetto, idSoggetto);
    CdsSoggettoServizio existing = cdsSoggettoServizioRepository.findById(Long.parseLong(cdsSoggettoServizioRequestDto.getId())).orElseThrow(
        () -> new AppException(AppError.CDS_SOGGETTO_SERVIZIO_NOT_FOUND, cdsSoggettoServizioRequestDto.getIdServizio(), idSoggetto)
    );
//    CdsSoggettoServizio existing = findCdsSubjectService(idSoggetto, idSoggettoServizio);
    requireIdServizio(cdsSoggettoServizioRequestDto.getIdServizio());
    CdsServizio servizio = findCdsServizio(cdsSoggettoServizioRequestDto.getIdServizio());
    // note: fk paStazionePa
    String stationFk = resolveStationFk(cdsSoggettoServizioRequestDto.getIdStazione(), soggetto.getCreditorInstitutionCode());

    existing.setFkCdsSoggetto(subjectObjectId);
    existing.setFkCdsServizio(String.valueOf(servizio.getId()));
    existing.setFkStazione(stationFk);
    existing.setIdSoggettoServizio(idSoggettoServizio);
    existing.setDescrizioneServizio(cdsSoggettoServizioRequestDto.getDescrizioneServizio());
    existing.setDataInizioValidita(cdsSoggettoServizioRequestDto.getDataInizioValidita());
    existing.setDataFineValidita(cdsSoggettoServizioRequestDto.getDataFineValidita());
    existing.setCommissione(cdsSoggettoServizioRequestDto.getCommissione());
    existing.setSoggetto(soggetto);
    existing.setServizio(servizio);
    existing.setStazionePa(resolveStation(cdsSoggettoServizioRequestDto.getIdStazione()));

    return toResponseCdsSoggettoServizio(saveAndRefresh(existing));
  }

  public void deleteCdsSubjectService(String idSoggetto, String idSoggettoServizio) {
    CdsSoggettoServizio existing = findCdsSubjectService(idSoggetto, idSoggettoServizio);
    cdsSoggettoServizioRepository.delete(existing);
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

  private CdsSoggettoServizio saveAndRefresh(CdsSoggettoServizio entity) {
    CdsSoggettoServizio saved = cdsSoggettoServizioRepository.saveAndFlush(entity);
    entityManager.refresh(saved);
    return saved;
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

  private CdsSoggetto findCdsSubjectByCreditorInstitutionCode(String creditorInstitutionCode) {
    requireCreditorInstitutionCode(creditorInstitutionCode);
    return findCdsSubjectByCreditorInstitutionCodeOptional(creditorInstitutionCode)
        .orElseThrow(() -> new AppException(AppError.CDS_SOGGETTO_NOT_FOUND, creditorInstitutionCode));
  }

  private Optional<CdsSoggetto> findCdsSubjectByCreditorInstitutionCodeOptional(
      String creditorInstitutionCode) {
    return cdsSoggettoRepository.findAll().stream()
        .filter(Objects::nonNull)
        .filter(elem -> Objects.equals(elem.getCreditorInstitutionCode(), creditorInstitutionCode))
        .findFirst();
  }

  private CdsSoggettoServizio findCdsSubjectService(String idSoggetto, String idSoggettoServizio) {
    String subjectObjectId = getSubjectObjectId(idSoggetto);
    requireIdSoggettoServizio(idSoggettoServizio);
    return findCdsSubjectServiceOptional(subjectObjectId, idSoggettoServizio)
        .orElseThrow(
            () ->
                new AppException(
                    AppError.CDS_SOGGETTO_SERVIZIO_NOT_FOUND, idSoggettoServizio, idSoggetto));
  }

  private Optional<CdsSoggettoServizio> findCdsSubjectServiceOptional(
      String subjectObjectId, String idSoggettoServizio) {
    return cdsSoggettoServizioRepository.findAllFetching().stream()
        .filter(Objects::nonNull)
        .filter(elem -> Objects.equals(elem.getFkCdsSoggetto(), subjectObjectId))
        .filter(elem -> Objects.equals(elem.getIdSoggettoServizio(), idSoggettoServizio))
        .findFirst();
  }

  private String getSubjectObjectId(String creditorInstitutionCode) {
    CdsSoggetto soggetto = findCdsSubjectByCreditorInstitutionCode(creditorInstitutionCode);
    return requireSubjectObjectId(soggetto, creditorInstitutionCode);
  }

  private String requireSubjectObjectId(CdsSoggetto soggetto, String creditorInstitutionCode) {
    if (soggetto.getId() == null) {
      throw new AppException(AppError.CDS_SOGGETTO_NOT_FOUND, creditorInstitutionCode);
    }
    return String.valueOf(soggetto.getId());
  }

  private CdsSoggettoServizio toResponseCdsSoggettoServizio(CdsSoggettoServizio cdsSoggettoServizio) {
    String idStazione = getStationIdFromFk(cdsSoggettoServizio.getFkStazione());
    return CdsSoggettoServizio.builder()
        .id(cdsSoggettoServizio.getId())
        .fkCdsSoggetto(cdsSoggettoServizio.getFkCdsSoggetto())
        .fkCdsServizio(cdsSoggettoServizio.getFkCdsServizio())
        .fkStazione(cdsSoggettoServizio.getFkStazione())
        .idSoggettoServizio(cdsSoggettoServizio.getIdSoggettoServizio())
        .descrizioneServizio(cdsSoggettoServizio.getDescrizioneServizio())
        .dataInizioValidita(cdsSoggettoServizio.getDataInizioValidita())
        .dataFineValidita(cdsSoggettoServizio.getDataFineValidita())
        .commissione(cdsSoggettoServizio.getCommissione())
        .stazionePa(
            idStazione == null
                ? null
                : PaStazionePa.builder().fkStazione(Stazioni.builder().idStazione(idStazione).build()).build())
        .servizio(getServizioFromFk(cdsSoggettoServizio.getFkCdsServizio()))
        .build();
  }

  private CdsServizio getServizioFromFk(String fkCdsServizio) {
    if (fkCdsServizio == null || fkCdsServizio.isBlank()) {
      return null;
    }
    try {
      Long servizioObjId = Long.valueOf(fkCdsServizio);
      return cdsServizioRepository.findAllFetching().stream()
          .filter(Objects::nonNull)
          .filter(elem -> Objects.equals(elem.getId(), servizioObjId))
          .findFirst()
          // build a lightweight, non-managed copy: the full entity's lazy "categoria"
          // association may be an uninitialized Hibernate proxy here, which breaks
          // JSON serialization (categoria isn't needed in this nested response anyway)
          .map(
              servizio ->
                  CdsServizio.builder()
                      .id(servizio.getId())
                      .idServizio(servizio.getIdServizio())
                      .descrizioneServizio(servizio.getDescrizioneServizio())
                      .build())
          .orElse(null);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private String getStationIdFromFk(String fkStazione) {
    if (fkStazione == null || fkStazione.isBlank()) {
      return null;
    }
    try {
      Long stationFkObjId = Long.valueOf(fkStazione);
      List<PaStazionePa> paStazionePaList = paStazionePaRepository.findAllFetching();
      if (paStazionePaList == null) {
        return null;
      }
      return paStazionePaList.stream()
          .filter(Objects::nonNull)
          .filter(elem -> Objects.equals(elem.getObjId(), stationFkObjId))
          .map(PaStazionePa::getFkStazione)
          .filter(Objects::nonNull)
          .map(Stazioni::getIdStazione)
          .filter(Objects::nonNull)
          .findFirst()
          .orElse(null);
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private String resolveStationFk(String idStazione, String idDominio) {
      if (idStazione == null || idStazione.isBlank()) {
          return null;
      }

      Pa ci = paRepository.findByIdDominio(idDominio).orElseThrow(
              () -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, idDominio)
      );

      Stazioni station = stazioniRepository.findByIdStazione(idStazione).orElseThrow(
              () -> new AppException(AppError.STATION_NOT_FOUND, idStazione)
      );

    PaStazionePa paStazionePa = paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(ci.getObjId(), station.getObjId()).orElseThrow(
            () -> new AppException(AppError.STATION_NOT_FOUND, idStazione)
    );

    return String.valueOf(paStazionePa.getObjId());
  }

  private PaStazionePa resolveStation(String idStazione) {
    if (idStazione == null || idStazione.isBlank()) {
      return null;
    }

    return paStazionePaRepository.findAllFetching().stream()
        .filter(Objects::nonNull)
        .filter(elem -> elem.getFkStazione() != null)
        .filter(elem -> Objects.equals(elem.getFkStazione().getIdStazione(), idStazione))
        .findFirst()
        .orElseThrow(() -> new AppException(AppError.STATION_NOT_FOUND, idStazione));
  }

  private void requireIdSoggettoServizio(String idSoggettoServizio) {
    if (idSoggettoServizio == null || idSoggettoServizio.isBlank()) {
      throw new AppException(AppError.CDS_SOGGETTO_SERVIZIO_BAD_REQUEST);
    }
  }

  private void requireCreditorInstitutionCode(String creditorInstitutionCode) {
    if (creditorInstitutionCode == null || creditorInstitutionCode.isBlank()) {
      throw new AppException(AppError.CDS_SOGGETTO_BAD_REQUEST);
    }
  }

  private Pa validateCreditorInstitutionExists(String creditorInstitutionCode) {
    return paRepository
        .findByIdDominio(creditorInstitutionCode)
        .orElseThrow(
            () ->
                new AppException(
                    AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
  }
}
