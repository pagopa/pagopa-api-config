package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Station;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Stations;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationsService {

    @Autowired
    IntermediariPaRepository intermediariPaRepository;

    @Autowired
    StazioniRepository stazioniRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private PaRepository paRepository;


    public Stations getStations(@NotNull Integer limit, @NotNull Integer pageNumber, String brokerCode, String creditorInstitutionCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Long fkIntermediario = null;
        Long fkPa = null;
        if (creditorInstitutionCode != null) {
            fkPa = getPaIfExists(creditorInstitutionCode).getObjId();
        }
        if (brokerCode != null) {
            fkIntermediario = getIntermediariPaIfExists(brokerCode).getObjId();
        }
        Page<Stazioni> page = stazioniRepository.findAllFilterByIntermediarioAndPa(fkIntermediario, fkPa, pageable);
        return Stations.builder()
                .stationsList(getStationsList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public StationDetails getStation(@NotNull String stationCode) {
        Stazioni stazione = getStationIfExists(stationCode);
        return modelMapper.map(stazione, StationDetails.class);
    }

    public StationDetails createStation(@NotNull StationDetails stationDetails) {
        if (stazioniRepository.findByIdStazione(stationDetails.getStationCode()).isPresent()) {
            throw new AppException(AppError.STATION_CONFLICT, stationDetails.getStationCode());
        }
        brokerCodeToObjId(stationDetails);
        Stazioni stazioni = modelMapper.map(stationDetails, Stazioni.class);
        stazioniRepository.save(stazioni);
        return stationDetails;
    }

    public StationDetails updateStation(@NotNull String stationCode, @NotNull StationDetails stationDetails) {
        Long objId = getStationIfExists(stationCode).getObjId();
        brokerCodeToObjId(stationDetails);
        Stazioni stazioni = modelMapper.map(stationDetails, Stazioni.class)
                .toBuilder()
                .objId(objId)
                .build();
        Stazioni result = stazioniRepository.save(stazioni);
        return modelMapper.map(result, StationDetails.class);
    }

    public void deleteStation(@NotNull String stationCode) {
        Stazioni stazioni = getStationIfExists(stationCode);
        stazioniRepository.delete(stazioni);
    }


    /**
     * Converts brokerCode in the stationDetails into objId and sets it in the stationDetails
     *
     * @param stationDetails details of station
     */
    private void brokerCodeToObjId(@NotNull StationDetails stationDetails) {
        IntermediariPa intermediariPa = getIntermediariPaIfExists(stationDetails.getBrokerCode());
        stationDetails.setBrokerObjId(intermediariPa.getObjId());
    }

    /**
     * @param brokerCode code of a broker
     * @return IntermediariPa if present else throws AppException
     */
    private IntermediariPa getIntermediariPaIfExists(String brokerCode) {
        return intermediariPaRepository.findByIdIntermediarioPa(brokerCode)
                .orElseThrow(() -> new AppException(AppError.BROKER_NOT_FOUND, brokerCode));
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
     * Maps all Stazioni stored in the DB in a List of Station
     *
     * @param page page of Stazioni returned from the database
     * @return a list of {@link Station}.
     */
    private List<Station> getStationsList(Page<Stazioni> page) {
        return page.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, Station.class))
                .collect(Collectors.toList());
    }

    /**
     * @param creditorInstitutionCode idDominio
     * @return return the PA record from DB if Exists
     * @throws AppException if not found
     */
    protected Pa getPaIfExists(String creditorInstitutionCode) throws AppException {
        return paRepository.findByIdDominio(creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
    }

}
