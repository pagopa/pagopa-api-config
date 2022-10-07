package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionList;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Iban;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ibans;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class CreditorInstitutionsService {

    public static final String BAD_RELATION_INFO = "Bad Relation info";
    @Autowired
    private PaRepository paRepository;

    @Autowired
    private StazioniRepository stazioniRepository;

    @Autowired
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    private IbanValidiPerPaRepository ibanValidiPerPaRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CreditorInstitutions getCreditorInstitutions(@NotNull Integer limit, @NotNull Integer pageNumber, @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, CommonUtil.getSort(filterAndOrder));
        var filters = CommonUtil.getFilters(Pa.builder()
                .idDominio(filterAndOrder.getFilter().getCode())
                .ragioneSociale(filterAndOrder.getFilter().getName())
                .build());
        Page<Pa> page = paRepository.findAll(filters, pageable);
        return CreditorInstitutions.builder()
                .creditorInstitutionList(getCreditorInstitutions(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public CreditorInstitutionDetails getCreditorInstitution(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

    @Transactional
    public CreditorInstitutionDetails createCreditorInstitution(@NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        if (paRepository.findByIdDominio(creditorInstitutionDetails.getCreditorInstitutionCode()).isPresent()) {
            throw new AppException(AppError.CREDITOR_INSTITUTION_CONFLICT, creditorInstitutionDetails.getCreditorInstitutionCode());
        }
        Pa pa = paRepository.save(modelMapper.map(creditorInstitutionDetails, Pa.class));
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

    public CreditorInstitutionDetails updateCreditorInstitution(@Size(max = 50) String creditorInstitutionCode, @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        Long objId = getPaIfExists(creditorInstitutionCode).getObjId();
        Pa pa = modelMapper.map(creditorInstitutionDetails, Pa.class).toBuilder()
                .objId(objId)
                .build();
        Pa result = paRepository.save(pa);
        return modelMapper.map(result, CreditorInstitutionDetails.class);
    }

    public void deleteCreditorInstitution(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        paRepository.delete(pa);
    }

    public CreditorInstitutionStationList getCreditorInstitutionStations(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<PaStazionePa> result = paStazionePaRepository.findAllByFkPa(pa.getObjId());
        return CreditorInstitutionStationList.builder()
                .stationsList(getStationsList(result))
                .build();
    }

    /**
     * Set the aux-digit to null if it is equals to 0 or 3
     *
     * @param creditorInstitutionStationEdit request
     */
    private static void setAuxDigitNull(CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        if (creditorInstitutionStationEdit.getAuxDigit().equals(0L) || creditorInstitutionStationEdit.getAuxDigit().equals(3L)) {
            creditorInstitutionStationEdit.setAuxDigit(null);
        }
    }

    @Transactional
    public CreditorInstitutionStationEdit createCreditorInstitutionStation(String creditorInstitutionCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // check aux-digit, application and segregation codes are configured properly
        checkAuxDigit(creditorInstitutionCode, creditorInstitutionStationEdit);
        // check if the relation already exists
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(creditorInstitutionStationEdit.getStationCode());
        if (paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId()).isPresent()) {
            throw new AppException(AppError.RELATION_STATION_CONFLICT, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode());
        }

        // check uniqueness rules
        checkSegregationCodePresent(creditorInstitutionStationEdit, pa, false);
        checkApplicationCodePresent(creditorInstitutionStationEdit, pa, false);

        // add info into object for model mapper
        setAuxDigitNull(creditorInstitutionStationEdit);
        creditorInstitutionStationEdit.setFkPa(pa);
        creditorInstitutionStationEdit.setFkStazioni(stazioni);

        // convert and save
        PaStazionePa entity = modelMapper.map(creditorInstitutionStationEdit, PaStazionePa.class);
        paStazionePaRepository.save(entity);
        return creditorInstitutionStationEdit;
    }

    @Transactional
    public void deleteCreditorInstitutionStation(String creditorInstitutionCode, String stationCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(stationCode);
        PaStazionePa paStazionePa = paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
                .orElseThrow(() -> new AppException(AppError.RELATION_STATION_NOT_FOUND, creditorInstitutionCode, stationCode));
        paStazionePaRepository.delete(paStazionePa);
    }

    public Ibans getCreditorInstitutionsIbans(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<IbanValidiPerPa> iban = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
        return Ibans.builder()
                .ibanList(getIbanList(iban))
                .build();
    }

    public CreditorInstitutionList getCreditorInstitutionsByIban(@NotNull String iban) {
        List<IbanValidiPerPa> items = ibanValidiPerPaRepository.findAllByIbanAccredito(iban);

        List<CreditorInstitution> ciList = items.isEmpty() ? new ArrayList<>() : items.stream().map(i -> paRepository.findById(i.getFkPa()))
                .filter(Optional::isPresent)
                .map(pa -> modelMapper.map(pa.get(), CreditorInstitution.class))
                .collect(Collectors.toList());

        return CreditorInstitutionList.builder()
                .creditorInstitutions(ciList)
                .build();
    }

    /**
     * Check application and segregation code according to aux-digit
     *
     * @param creditorInstitutionCode        creditor institution code
     * @param creditorInstitutionStationEdit relation info
     */
    private void checkAuxDigit(String creditorInstitutionCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        if (!Arrays.asList(0L, 1L, 2L, 3L).contains(creditorInstitutionStationEdit.getAuxDigit())) {
            String message = "AugDigit code error: accepted values are 0, 1, 2, 3";
            throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
        }

        if (creditorInstitutionStationEdit.getAuxDigit().equals(0L)) {
            checkAuxDigit0(creditorInstitutionCode, creditorInstitutionStationEdit);
        } else if (creditorInstitutionStationEdit.getAuxDigit().equals(1L) || creditorInstitutionStationEdit.getAuxDigit().equals(2L)) {
            if (creditorInstitutionStationEdit.getApplicationCode() != null) {
                String message = "Application code error: length must be blank";
                throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
            }

            if (creditorInstitutionStationEdit.getSegregationCode() != null) {
                String message = "Segregation code error: length must be blank";
                throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
            }
        } else if (creditorInstitutionStationEdit.getAuxDigit().equals(3L)) {
            checkAuxDigit3(creditorInstitutionCode, creditorInstitutionStationEdit);
        }
    }

    private void checkAuxDigit0(String creditorInstitutionCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // if aux digit is equal to 0,
        // application code must be 2 ciphers
        // segregation code should be blank or 2 ciphers
        // however they have Long type, so we can check if they have at most 2 cipher
        if (creditorInstitutionStationEdit.getApplicationCode() == null ||
                (creditorInstitutionStationEdit.getApplicationCode().toString().length() < 1 || creditorInstitutionStationEdit.getApplicationCode().toString().length() > 2)) {
            String message = "Application code error: length must be 2 ciphers";
            throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
        }

        if (creditorInstitutionStationEdit.getSegregationCode() != null &&
                (creditorInstitutionStationEdit.getSegregationCode().toString().length() < 1 || creditorInstitutionStationEdit.getSegregationCode().toString().length() > 2)) {
            String message = "Segregation code error: length must be 2 ciphers or blank";
            throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
        }
    }

    private void checkAuxDigit3(String creditorInstitutionCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // if aux digit is equal to 3,
        // application code should be 2 ciphers or blank
        // segregation code must be 2 ciphers
        // however they have Long type, so we can check if they have at most 2 cipher
        if (creditorInstitutionStationEdit.getApplicationCode() != null &&
                (creditorInstitutionStationEdit.getApplicationCode().toString().length() < 1 || creditorInstitutionStationEdit.getApplicationCode().toString().length() > 2)) {
            String message = "Application code error: length must be 2 ciphers or blank";
            throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
        }

        if (creditorInstitutionStationEdit.getSegregationCode() == null ||
                (creditorInstitutionStationEdit.getSegregationCode().toString().length() < 1 || creditorInstitutionStationEdit.getSegregationCode().toString().length() > 2)) {
            String message = "Segregation code error: length must be 2 ciphers";
            throw new AppException(AppError.RELATION_STATION_BAD_REQUEST, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode(), message);
        }
    }

    /**
     * @param stationCode idStazione
     * @return return the Stazioni record from DB if Exists
     * @throws AppException if not found
     */
    private Stazioni getStazioniIfExists(String stationCode) {
        return stazioniRepository.findByIdStazione(stationCode)
                .orElseThrow(() -> new AppException(AppError.STATION_NOT_FOUND, stationCode));
    }

    /**
     * @param creditorInstitutionCode idDominio
     * @return return the PA record from DB if Exists
     * @throws AppException if not found
     */
    protected Pa getPaIfExists(String creditorInstitutionCode) throws AppException {
        Optional<Pa> result = paRepository.findByIdDominio(creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode);
        }
        return result.get();
    }


    /**
     * Maps a list of PaStazionePa into a list of CreditorInstitutionStation
     *
     * @param paStazionePaList list of {@link PaStazionePa}
     * @return the list of {@link CreditorInstitutionStation}
     */
    private List<CreditorInstitutionStation> getStationsList(List<PaStazionePa> paStazionePaList) {
        return paStazionePaList.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, CreditorInstitutionStation.class))
                .collect(Collectors.toList());
    }

    /**
     * Maps PA objects stored in the DB in a List of CreditorInstitution
     *
     * @param page page of PA returned from the database
     * @return a list of {@link CreditorInstitutionDetails}.
     */
    private List<CreditorInstitution> getCreditorInstitutions(Page<Pa> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, CreditorInstitution.class))
                .collect(Collectors.toList());
    }


    /**
     * @param ibans List of valid ibans
     * @return map elements into a list of {@link Iban}
     */
    private List<Iban> getIbanList(List<IbanValidiPerPa> ibans) {
        return ibans.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, Iban.class))
                .collect(Collectors.toList());
    }

    private void checkApplicationCodePresent(CreditorInstitutionStationEdit creditorInstitutionStationEdit, Pa pa, boolean edit) {
        if (creditorInstitutionStationEdit.getAuxDigit() == 3L) {
            if (creditorInstitutionStationEdit.getApplicationCode() != null && !paStazionePaRepository.findAllByFkPaAndProgressivo(pa.getObjId(), creditorInstitutionStationEdit.getApplicationCode()).isEmpty()) {
                throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "ApplicationCode already exists");
            }
        } else if (edit && !paStazionePaRepository.findAllByFkPaAndSegregazioneAndFkStazione_IdStazioneIsNot(pa.getObjId(), creditorInstitutionStationEdit.getApplicationCode(), creditorInstitutionStationEdit.getStationCode()).isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "ApplicationCode already exists");
        } else if (!edit && !paStazionePaRepository.findAllByFkPaAndProgressivo(pa.getObjId(), creditorInstitutionStationEdit.getApplicationCode()).isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "ApplicationCode already exists");
        }

    }

    private void checkSegregationCodePresent(CreditorInstitutionStationEdit creditorInstitutionStationEdit, Pa pa, boolean edit) {
        if (creditorInstitutionStationEdit.getAuxDigit() == 0L) {
            if (creditorInstitutionStationEdit.getSegregationCode() != null &&
                    !paStazionePaRepository.findAllByFkPaAndSegregazione(pa.getObjId(), creditorInstitutionStationEdit.getSegregationCode()).isEmpty()) {
                throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "SegregationCode already exists");
            }
        } else if (edit && !paStazionePaRepository.findAllByFkPaAndSegregazioneAndFkStazione_IdStazioneIsNot(pa.getObjId(), creditorInstitutionStationEdit.getSegregationCode(), creditorInstitutionStationEdit.getStationCode()).isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "SegregationCode already exists");
        } else if (!edit && !paStazionePaRepository.findAllByFkPaAndSegregazione(pa.getObjId(), creditorInstitutionStationEdit.getSegregationCode()).isEmpty()) {
            throw new AppException(HttpStatus.CONFLICT, BAD_RELATION_INFO, "SegregationCode already exists");
        }
    }

    @Transactional
    public CreditorInstitutionStationEdit updateCreditorInstitutionStation(String creditorInstitutionCode, String stationCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // check if the relation exists
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(stationCode);
        PaStazionePa paStazionePa = paStazionePaRepository.findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
                .orElseThrow(() -> new AppException(AppError.RELATION_STATION_NOT_FOUND, creditorInstitutionCode, stationCode));

        // check aux-digit, application and segregation codes are configured properly
        checkAuxDigit(creditorInstitutionCode, creditorInstitutionStationEdit);

        // check uniqueness rules
        checkSegregationCodePresent(creditorInstitutionStationEdit, pa, true);
        checkApplicationCodePresent(creditorInstitutionStationEdit, pa, true);

        // add info into object for model mapper
        setAuxDigitNull(creditorInstitutionStationEdit);
        creditorInstitutionStationEdit.setFkPa(pa);
        creditorInstitutionStationEdit.setFkStazioni(stazioni);

        // convert and save
        PaStazionePa entity = modelMapper.map(creditorInstitutionStationEdit, PaStazionePa.class)
                .toBuilder()
                .objId(paStazionePa.getObjId())
                .build();
        paStazionePaRepository.save(entity);
        return creditorInstitutionStationEdit;
    }


}
