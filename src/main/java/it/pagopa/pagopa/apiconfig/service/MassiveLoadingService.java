package it.pagopa.pagopa.apiconfig.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvException;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.massiveloading.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.util.CreditorInstitutionStationVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
@Validated
@Slf4j
public class MassiveLoadingService {

    @Value("${properties.environment}")
    private String environment;

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    private StazioniRepository stazioniRepository;

    @Autowired
    CreditorInstitutionsService creditorInstitutionsService;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void manageCIStation(MultipartFile file) {
        // read CSV

        try {
            Reader reader = new StringReader(new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8));

            // create mapping strategy to arrange the column name
            HeaderColumnNameMappingStrategy<CreditorInstitutionStation> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
            mappingStrategy.setType(CreditorInstitutionStation.class);

            CsvToBean<CreditorInstitutionStation> parsedCSV = new CsvToBeanBuilder<CreditorInstitutionStation>(reader)
                    .withSeparator(',')
                    .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH)
                    .withOrderedResults(true)
                    .withMappingStrategy(mappingStrategy)
                    .withVerifier(new CreditorInstitutionStationVerifier(environment, paRepository, stazioniRepository, paStazionePaRepository))
                    .withType(CreditorInstitutionStation.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withThrowExceptions(false)
                    .build();

            List<CreditorInstitutionStation> items = parsedCSV.parse();
            List<CsvException> errors = parsedCSV.getCapturedExceptions();

            if (errors.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                errors.forEach(error -> stringBuilder.append(String.format("|%s |", error.getMessage())));
                throw new AppException(AppError.MASSIVELOADING_BAD_REQUEST, stringBuilder);
            }

            // validation executed successfully, items could be added or deleted according to the specified operation

            for (CreditorInstitutionStation item : items) {
                log.warn("-> Item {} {}", item.getCreditorInstitutionId(), item.getStationId());
                if (item.getOperation().equals(CreditorInstitutionStation.Operation.A)) {
                    Long segregationCode = item.getSegregationCode() != null ? Long.parseLong(item.getSegregationCode()) : null;
                    Long applicationCode = item.getApplicationCode() != null ? Long.parseLong(item.getApplicationCode()) : null;
                    Long auxDigit = item.getAuxDigit() == 0 || item.getAuxDigit() == 3 ? null : item.getAuxDigit();
                    CreditorInstitutionStationEdit data = CreditorInstitutionStationEdit.builder()
                            .stationCode(item.getStationId())
                            .auxDigit(auxDigit)
                            .applicationCode(applicationCode)
                            .segregationCode(segregationCode)
                            .broadcast(item.getBroadcast() == CreditorInstitutionStation.YesNo.S)
                            .mod4(false)
                            .build();
                    creditorInstitutionsService.createCreditorInstitutionStation(item.getCreditorInstitutionId(), data);
                }
                else if (item.getOperation().equals(CreditorInstitutionStation.Operation.C)) {
                    creditorInstitutionsService.deleteCreditorInstitutionStation(item.getCreditorInstitutionId(), item.getStationId());
                }
            }
        } catch (IOException e) {
            throw new AppException(AppError.MASSIVELOADING_BAD_REQUEST, e, e.getMessage());
        }
    }
}
