package it.pagopa.pagopa.apiconfig.util;

import com.opencsv.bean.BeanVerifier;
import com.opencsv.exceptions.CsvConstraintViolationException;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
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
    private final PaRepository paRepository;
    private final StazioniRepository stazioniRepository;
    private final PaStazionePaRepository paStazionePaRepository;

    public CreditorInstitutionStationVerifier(String environment,
                                              PaRepository paRepository, StazioniRepository stazioniRepository, PaStazionePaRepository paStazionePaRepository) {
        this.paRepository = paRepository;
        this.stazioniRepository = stazioniRepository;
        this.paStazionePaRepository = paStazionePaRepository;

        if (environment.equalsIgnoreCase("PROD")) {
            this.env = CreditorInstitutionStation.Env.ESER;
        } else {
            this.env = CreditorInstitutionStation.Env.COLL;
        }

    }

    /**
     * @param creditorInstitutionStation Station ID
     * @return true if the bean is valid
     * @throws CsvConstraintViolationException with the list of errors
     */
    @Override
    public boolean verifyBean(CreditorInstitutionStation creditorInstitutionStation) throws CsvConstraintViolationException {

        // skip validation if the current row is for a different environment
        if (!creditorInstitutionStation.getEnvironment().equals(this.env)) {
            return false;
        }

        List<String> errors = new ArrayList<>();

        // verify creditor institution
        Optional<Pa> optEc = checkCreditorInstitution(creditorInstitutionStation, errors);

        // verify station
        Optional<Stazioni> optStation = checkStation(creditorInstitutionStation, errors);

        // check auxDigit
        checkAuxDigit(creditorInstitutionStation, errors);

        // check segregationCode and applicationCode according to auxDigit
        checkSegregationAndApplicationCode(creditorInstitutionStation, errors);

        if (optEc.isPresent() && optStation.isPresent() && creditorInstitutionStation.getOperation().equals(CreditorInstitutionStation.Operation.A)) {
            // verify if relationship already exists
            checkIfExists(errors, optEc.get(), optStation.get());
        } else if (optEc.isPresent() && optStation.isPresent() && creditorInstitutionStation.getOperation().equals(CreditorInstitutionStation.Operation.C)) {
            // verify if exists the configuration in order to delete
            checkIfFound(creditorInstitutionStation, errors, optEc.get(), optStation.get());
        }

        if (!errors.isEmpty()) {
            String messageFormat = "[Line %s] %s" + System.lineSeparator();
            throw new CsvConstraintViolationException(String.format(messageFormat,
                    ToStringBuilder.reflectionToString(creditorInstitutionStation, ToStringStyle.NO_CLASS_NAME_STYLE),
                    String.join(" # ", errors)));
        }

        return true;
    }

    /**
     * add error in the list if relationship not found
     *
     * @param creditorInstitutionStation Station details to check
     * @param errors                     list of errors
     * @param optEc                      Creditor Institution
     * @param optStation                 Station
     */
    private void checkIfFound(CreditorInstitutionStation creditorInstitutionStation, List<String> errors, Pa optEc, Stazioni optStation) {
        Long segregationCode = creditorInstitutionStation.getSegregationCode() != null ? Long.parseLong(creditorInstitutionStation.getSegregationCode()) : null;
        Long applicationCode = creditorInstitutionStation.getApplicationCode() != null ? Long.parseLong(creditorInstitutionStation.getApplicationCode()) : null;
        Optional<PaStazionePa> optECStation = paStazionePaRepository.findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
                optEc.getObjId(), optStation.getObjId(), creditorInstitutionStation.getAuxDigit(),
                creditorInstitutionStation.getBroadcast() == CreditorInstitutionStation.YesNo.S,
                segregationCode, applicationCode);
        if (optECStation.isEmpty()) {
            errors.add("Creditor Institution - Station relationship not found");
        }
    }

    /**
     * add error in the list if relationship already exists
     *
     * @param errors     list of errors
     * @param optEc      optional creditor institution
     * @param optStation optional station
     */
    private void checkIfExists(List<String> errors, Pa optEc, Stazioni optStation) {
        Optional<PaStazionePa> relation = paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(optEc.getObjId(), optStation.getObjId());
        if (relation.isPresent()) {
            errors.add("Creditor institution - Station relationship already exists");
        }
    }

    /**
     * add error in the list if needed
     *
     * @param creditorInstitutionStation the creditor institution station to check
     * @param errors                     list of errors
     */
    private void checkSegregationAndApplicationCode(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        if (creditorInstitutionStation.getAuxDigit() == 1 || creditorInstitutionStation.getAuxDigit() == 2) {
            if (!creditorInstitutionStation.getApplicationCode().isBlank()) {
                errors.add("Application code error: length must be blank");
            }

            if (!creditorInstitutionStation.getSegregationCode().isBlank()) {
                errors.add("Segregation code error: length must be blank");
            }
        } else if (creditorInstitutionStation.getAuxDigit() == 0) {
            checkSegregationAndApplicationCode0(creditorInstitutionStation, errors);
        } else if (creditorInstitutionStation.getAuxDigit() == 3) {
            checkSegregationAndApplicationCode3(creditorInstitutionStation, errors);
        }
        var pa = paRepository.findByIdDominio(creditorInstitutionStation.getCreditorInstitutionId());
        if (pa.isPresent()) {
            if (creditorInstitutionStation.getSegregationCode() != null
                    && !paStazionePaRepository.findAllByFkPaAndSegregazione(pa.get().getObjId(),
                    Long.parseLong(creditorInstitutionStation.getSegregationCode())).isEmpty()) {
                errors.add("segregationCode already exists");
            }
            if (creditorInstitutionStation.getApplicationCode() != null
                    && !paStazionePaRepository.findAllByFkPaAndProgressivo(pa.get().getObjId(),


                    Long.parseLong(creditorInstitutionStation.getApplicationCode())).isEmpty()) {
                errors.add("applicationCode already exists");
            }
        }

    }

    private void checkSegregationAndApplicationCode0(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        if (creditorInstitutionStation.getApplicationCode() == null || creditorInstitutionStation.getApplicationCode().isBlank() ||
                creditorInstitutionStation.getApplicationCode().length() != 2) {
            errors.add("Application code error: length must be 2 ciphers");
        }

        if (creditorInstitutionStation.getSegregationCode() != null &&
                !creditorInstitutionStation.getSegregationCode().isBlank() && creditorInstitutionStation.getSegregationCode().length() != 2) {
            errors.add("Segregation code error: length must be 2 ciphers or blank");
        }
    }

    private void checkSegregationAndApplicationCode3(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        if (creditorInstitutionStation.getApplicationCode() != null &&
                !creditorInstitutionStation.getApplicationCode().isBlank() && creditorInstitutionStation.getApplicationCode().length() != 2) {
            errors.add("Application code error: length must be 2 ciphers or blank");
        }

        if (creditorInstitutionStation.getSegregationCode() == null || creditorInstitutionStation.getSegregationCode().isBlank() ||
                creditorInstitutionStation.getSegregationCode().length() != 2) {
            errors.add("Segregation code error: length must be 2 ciphers");
        }
    }

    /**
     * add error in the list if aux digit is not 0,1,2 or 3
     *
     * @param creditorInstitutionStation the creditor institution station to check
     * @param errors                     list of errors
     */
    private void checkAuxDigit(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        if (!Arrays.asList(0L, 1L, 2L, 3L).contains(creditorInstitutionStation.getAuxDigit())) {
            errors.add("AugDigit code error: accepted values are 0, 1, 2, 3");
        }
    }

    /**
     * @param creditorInstitutionStation the creditor institution station to check
     * @param errors                     list of errors
     * @return the optional station and add error in the list if needed
     */
    private Optional<Stazioni> checkStation(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        Optional<Stazioni> optStation = stazioniRepository.findByIdStazione(creditorInstitutionStation.getStationId());
        if (optStation.isEmpty()) {
            errors.add("Station not exists");
        }
        return optStation;
    }

    /**
     * @param creditorInstitutionStation the creditor institution station to check
     * @param errors                     list of errors
     * @return the optional creditor institution and add error in the list if needed
     */
    private Optional<Pa> checkCreditorInstitution(CreditorInstitutionStation creditorInstitutionStation, List<String> errors) {
        Optional<Pa> optEc = paRepository.findByIdDominio(creditorInstitutionStation.getCreditorInstitutionId());
        if (optEc.isEmpty()) {
            errors.add("Creditor institution not exists");
        }
        return optEc;
    }
}
