package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.*;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class StationsService {

    @Autowired
    IntermediariPaRepository intermediariPaRepository;

    @Autowired
    StazioniRepository stazioniRepository;

    @Autowired
    PaStazionePaRepository paStazioniRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private PaRepository paRepository;


    public Stations getStations(@NotNull Integer limit, @NotNull Integer pageNumber,
                                @Nullable String brokerCode, @Nullable String creditorInstitutionCode,
                                @Valid FilterAndOrder filterAndOrder) {
        Pageable pageable = PageRequest.of(pageNumber, limit, CommonUtil.getSort(filterAndOrder));
        // convert code to FK
        Long fkIntermediario = Optional.ofNullable(brokerCode)
                .map(elem -> getIntermediariPaIfExists(elem).getObjId())
                .orElse(null);
        Long fkPa = Optional.ofNullable(creditorInstitutionCode)
                .map(elem -> getPaIfExists(elem).getObjId())
                .orElse(null);

        Page<Stazioni> page = queryStazioni(fkIntermediario, fkPa, filterAndOrder, pageable);
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

    public StationCreditorInstitutions getStationCreditorInstitutions(@NotNull String stationCode, @NotNull Integer limit, @NotNull Integer pageNumber) {
        Stazioni stazioni = getStationIfExists(stationCode);
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<PaStazionePa> page = paStazioniRepository.findAllByFkStazione_ObjId(stazioni.getObjId(), pageable);
        List<StationCreditorInstitution> ecList = page.stream().map(paStazionePa -> modelMapper.map(paStazionePa, StationCreditorInstitution.class)).collect(Collectors.toList());
        return StationCreditorInstitutions.builder()
                .pageInfo(CommonUtil.buildPageInfo(page))
                .creditorInstitutionList(ecList)
                .build();
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


    /**
     * @param fkIntermediario filter by intermediariPa ('equals' comparison)
     * @param fkPa            filter by PA ('equals' comparison)
     * @param filterAndOrder  filter by CODE ('like' comparison)
     * @param pageable        page and sort info
     * @return the result page of the query
     */
    private Page<Stazioni> queryStazioni(Long fkIntermediario, Long fkPa, FilterAndOrder filterAndOrder, Pageable pageable) {
        if (fkPa != null) {
            return stazioniRepository.findAllByFilters(fkIntermediario, fkPa, filterAndOrder.getFilter().getCode(), pageable);
        } else {
            return stazioniRepository.findAllByFilters(fkIntermediario, filterAndOrder.getFilter().getCode(), pageable);
        }
    }
}
