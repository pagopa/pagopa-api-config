package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationEdit;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Iban;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ibans;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CreditorInstitutionsService {

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


    public CreditorInstitutions getCreditorInstitutions(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Pa> page = paRepository.findAll(pageable);
        return CreditorInstitutions.builder()
                .creditorInstitutionList(getCreditorInstitutions(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public CreditorInstitutionDetails getCreditorInstitution(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

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
        List<PaStazionePa> result = paStazionePaRepository.findAllByFkPa_ObjId(pa.getObjId());
        return CreditorInstitutionStationList.builder()
                .stationsList(getStationsList(result))
                .build();
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public CreditorInstitutionStationEdit createCreditorInstitutionStation(String creditorInstitutionCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // check if the relation already exists
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(creditorInstitutionStationEdit.getStationCode());
        if (paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId()).isPresent()) {
            throw new AppException(AppError.RELATION_STATION_CONFLICT, creditorInstitutionCode, creditorInstitutionStationEdit.getStationCode());
        }
        // add info into object for model mapper
        creditorInstitutionStationEdit.setFkPa(pa);
        creditorInstitutionStationEdit.setFkStazioni(stazioni);

        // convert and save
        PaStazionePa entity = modelMapper.map(creditorInstitutionStationEdit, PaStazionePa.class);
        paStazionePaRepository.save(entity);
        return creditorInstitutionStationEdit;
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public CreditorInstitutionStationEdit updateCreditorInstitutionStation(String creditorInstitutionCode, String stationCode, CreditorInstitutionStationEdit creditorInstitutionStationEdit) {
        // check if the relation exists
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(stationCode);
        PaStazionePa paStazionePa = paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
                .orElseThrow(() -> new AppException(AppError.RELATION_STATION_NOT_FOUND, creditorInstitutionCode, stationCode));
        // add info into object for model mapper
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

    @Transactional(isolation = Isolation.DEFAULT)
    public void deleteCreditorInstitutionStation(String creditorInstitutionCode, String stationCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        Stazioni stazioni = getStazioniIfExists(stationCode);
        PaStazionePa paStazionePa = paStazionePaRepository.findAllByFkPa_ObjIdAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId())
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


}
