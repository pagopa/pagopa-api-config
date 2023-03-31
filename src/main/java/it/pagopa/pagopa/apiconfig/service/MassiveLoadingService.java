package it.pagopa.pagopa.apiconfig.service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvException;

import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CsvMassiveMigration;
import it.pagopa.pagopa.apiconfig.model.massiveloading.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.util.CreditorInstitutionStationVerifier;

@Service
@Valid
@Transactional
public class MassiveLoadingService {

  @Value("${info.properties.environment}")
  private String environment;

  @Autowired private PaRepository paRepository;

  @Autowired private PaStazionePaRepository paStazionePaRepository;

  @Autowired private StazioniRepository stazioniRepository;

  @Autowired CreditorInstitutionsService creditorInstitutionsService;

  public void manageCIStation(MultipartFile file) {
    try {
      addOrDelete(file);
    } catch (IOException | RuntimeException e) {
      throw new AppException(AppError.MASSIVELOADING_BAD_REQUEST, e, e.getMessage());
    }
  }

  public void massiveMigration(MultipartFile file) {
    // read CSV
    Reader reader;
    try {
      reader =
          new StringReader(
              new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }

    // create mapping strategy to arrange the column name
    HeaderColumnNameMappingStrategy<CsvMassiveMigration> mappingStrategy =
        new HeaderColumnNameMappingStrategy<>();
    mappingStrategy.setType(CsvMassiveMigration.class);

    // execute validation
    List<CsvMassiveMigration> items;
    try {
      CsvToBean<CsvMassiveMigration> parsedCSV =
          new CsvToBeanBuilder<CsvMassiveMigration>(reader)
              .withSeparator(',')
              .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
              .withOrderedResults(true)
              .withMappingStrategy(mappingStrategy)
              .withType(CsvMassiveMigration.class)
              .withIgnoreLeadingWhiteSpace(true)
              .withThrowExceptions(true)
              .build();

      items = parsedCSV.parse();
    } catch (Exception e) {
      throw new AppException(AppError.MASSIVELOADING_BAD_REQUEST, e, e.getMessage());
    }

    for (CsvMassiveMigration item : items) {
      Stazioni oldStation = getStationIfExists(item.getOldStation());
      Stazioni newStation = getStationIfExists(item.getNewStation());
      Pa pa = getPaIfExists(item.getCreditorInstitution());

      var relationWithOldStation =
          paStazionePaRepository
              .findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), oldStation.getObjId())
              .orElseThrow(
                  () ->
                      new AppException(
                          AppError.RELATION_STATION_NOT_FOUND,
                          item.getCreditorInstitution(),
                          item.getOldStation()));

      var relationWithNewStation =
          paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(
              pa.getObjId(), newStation.getObjId());
      if (relationWithNewStation.isPresent()) {
        throw new AppException(
            AppError.RELATION_STATION_CONFLICT,
            item.getCreditorInstitution(),
            item.getNewStation());
      }

      var builder = relationWithOldStation.toBuilder().fkStazione(newStation);
      if (item.getBroadcast() != null) {
        builder.broadcast(item.getBroadcast().isValue());
      }
      var entity = builder.build();
      if (Boolean.TRUE.equals(entity.getBroadcast()) && !newStation.getVersione().equals(2L)) {
        throw new AppException(
            AppError.MASSIVELOADING_BAD_REQUEST,
            "station version must be equals to 2 if broadcast is YES");
      }
      paStazionePaRepository.save(entity);
    }
  }

  /**
   * add or remove all elements in the file
   *
   * @param file csv file
   * @throws IOException if file is not readable
   */
  private void addOrDelete(MultipartFile file) throws IOException {
    // read CSV
    Reader reader =
        new StringReader(new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

    // create mapping strategy to arrange the column name
    HeaderColumnNameMappingStrategy<CreditorInstitutionStation> mappingStrategy =
        new HeaderColumnNameMappingStrategy<>();
    mappingStrategy.setType(CreditorInstitutionStation.class);

    // execute validation
    CsvToBean<CreditorInstitutionStation> parsedCSV =
        new CsvToBeanBuilder<CreditorInstitutionStation>(reader)
            .withSeparator(',')
            .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
            .withOrderedResults(true)
            .withMappingStrategy(mappingStrategy)
            .withVerifier(
                new CreditorInstitutionStationVerifier(
                    environment, paRepository, stazioniRepository, paStazionePaRepository))
            .withType(CreditorInstitutionStation.class)
            .withIgnoreLeadingWhiteSpace(true)
            .withThrowExceptions(false)
            .build();

    List<CreditorInstitutionStation> items = parsedCSV.parse();
    List<CsvException> errors = parsedCSV.getCapturedExceptions();

    if (!errors.isEmpty()) {
      StringBuilder stringBuilder = new StringBuilder();
      errors.forEach(error -> stringBuilder.append(String.format("|%s |", error.getMessage())));
      throw new AppException(AppError.MASSIVELOADING_BAD_REQUEST, stringBuilder);
    }

    // validation executed successfully, items could be added or deleted according to the specified
    // operation

    for (CreditorInstitutionStation item : items) {
      if (item.getOperation().equals(CreditorInstitutionStation.Operation.A)) {
        Long segregationCode =
            item.getSegregationCode() != null ? Long.parseLong(item.getSegregationCode()) : null;
        Long applicationCode =
            item.getApplicationCode() != null ? Long.parseLong(item.getApplicationCode()) : null;

        CreditorInstitutionStationEdit data =
            CreditorInstitutionStationEdit.builder()
                .stationCode(item.getStationId())
                .auxDigit(item.getAuxDigit())
                .applicationCode(applicationCode)
                .segregationCode(segregationCode)
                .broadcast(item.getBroadcast() == CreditorInstitutionStation.YesNo.S)
                .mod4(false)
                .build();
        creditorInstitutionsService.createCreditorInstitutionStation(
            item.getCreditorInstitutionId(), data);
      } else if (item.getOperation().equals(CreditorInstitutionStation.Operation.C)) {
        creditorInstitutionsService.deleteCreditorInstitutionStation(
            item.getCreditorInstitutionId(), item.getStationId());
      }
    }
  }

  /**
   * @param stationCode code of the station
   * @return search on DB using the {@code stationCode} and return the Stazioni if it is present
   * @throws AppException if not found
   */
  private Stazioni getStationIfExists(String stationCode) {
    Optional<Stazioni> result = stazioniRepository.findByIdStazione(stationCode);
    if (result.isEmpty()) {
      throw new AppException(AppError.STATION_NOT_FOUND, stationCode);
    }
    return result.get();
  }

  /**
   * @param creditorInstitutionCode idDominio
   * @return return the PA record from DB if Exists
   * @throws AppException if not found
   */
  protected Pa getPaIfExists(String creditorInstitutionCode) throws AppException {
    return paRepository
        .findByIdDominio(creditorInstitutionCode)
        .orElseThrow(
            () ->
                new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
  }
}
