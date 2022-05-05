package it.pagopa.pagopa.apiconfig.util;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.massiveloading.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CreditorInstitutionStationVerifier implements BeanVerifier<CreditorInstitutionStation> {

    private final CreditorInstitutionStation.Env env;
    private PaRepository paRepository;
    private StazioniRepository stazioniRepository;
    private PaStazionePaRepository paStazionePaRepository;

    public CreditorInstitutionStationVerifier(String environment,
                                              PaRepository paRepository, StazioniRepository stazioniRepository, PaStazionePaRepository paStazionePaRepository) {
        this.paRepository = paRepository;
        this.stazioniRepository = stazioniRepository;
        this.paStazionePaRepository = paStazionePaRepository;

        if (environment.equalsIgnoreCase("PROD")) {
            this.env = CreditorInstitutionStation.Env.ESER;
        }
        else {
            this.env = CreditorInstitutionStation.Env.COLL;
        }

    }

    @Override
    public boolean verifyBean(CreditorInstitutionStation creditorInstitutionStation) throws CsvConstraintViolationException {

        // skip validation if the current row is for a different environment
        if (!creditorInstitutionStation.getEnvironment().equals(this.env)) {
            return false;
        }

        List<String> errors = new ArrayList<>();

        // verify creditor institution
        Optional<Pa> optEc = paRepository.findByIdDominio(creditorInstitutionStation.getCreditorInstitutionId());
        if (optEc.isEmpty()) {
            errors.add("Creditor institution not exists");
        }

        // verify station
        Optional<Stazioni> optStation = stazioniRepository.findByIdStazione(creditorInstitutionStation.getStationId());
        if (optStation.isEmpty()) {
            errors.add("Station not exists");
        }

        // check auxDigit
        if (!Arrays.asList(0L, 1L, 2L, 3L).contains(creditorInstitutionStation.getAuxDigit())) {
            errors.add("AugDigit code error: accepted values are 0, 1, 2, 3");
        }

        // check segregationCode and applicationCode according to auxDigit
        if (creditorInstitutionStation.getAuxDigit() == 3) {
            if (creditorInstitutionStation.getApplicationCode() != null &&
                    !creditorInstitutionStation.getApplicationCode().isBlank() && creditorInstitutionStation.getApplicationCode().length() != 2) {
                errors.add("Aggregation code error: length must be 2 or blank");
            }

            if (creditorInstitutionStation.getSegregationCode() != null &&
                    !creditorInstitutionStation.getSegregationCode().isBlank() && creditorInstitutionStation.getSegregationCode().length() != 2) {
                errors.add("Segregation code error: length must be 2 or blank");
            }
        }


        if (optEc.isPresent() && optStation.isPresent() && creditorInstitutionStation.getOperation().equals(CreditorInstitutionStation.Operation.A)) {
            // verify if relationship already exists
            Optional<PaStazionePa> relation = paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(optEc.get().getObjId(), optStation.get().getObjId());
            if (relation.isPresent()) {
                errors.add("Creditor institution - Station relationship already exists");
            }
        }
        else if (optEc.isPresent() && optStation.isPresent() && creditorInstitutionStation.getOperation().equals(CreditorInstitutionStation.Operation.C)) {
            // verify if exists the configuration in order to delete
            Long segregationCode = creditorInstitutionStation.getSegregationCode() != null ? Long.parseLong(creditorInstitutionStation.getSegregationCode()) : null;
            Long applicationCode = creditorInstitutionStation.getApplicationCode() != null ? Long.parseLong(creditorInstitutionStation.getApplicationCode()) : null;
            Long auxDigit = creditorInstitutionStation.getAuxDigit() == 0 || creditorInstitutionStation.getAuxDigit() == 3 ? null : creditorInstitutionStation.getAuxDigit();
            Optional<PaStazionePa> optECStation = paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndAndBroadcastAndSegregazioneAndProgressivo(
                    optEc.get().getObjId(), optStation.get().getObjId(), auxDigit,
                    creditorInstitutionStation.getBroadcast() == CreditorInstitutionStation.YesNo.S,
                    segregationCode, applicationCode);
            if (optECStation.isEmpty()) {
                errors.add("Creditor Institution - Station relationship not found");
            }
        }

        if (!errors.isEmpty()) {
            throw new CsvConstraintViolationException(String.format("[Line %s] %s" + System.lineSeparator(),
                    ToStringBuilder.reflectionToString(creditorInstitutionStation, ToStringStyle.NO_CLASS_NAME_STYLE),
                    String.join(" # ", errors)));
        }

        return true;
    }
}
