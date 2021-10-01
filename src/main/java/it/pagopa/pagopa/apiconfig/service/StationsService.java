package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.Station;
import it.pagopa.pagopa.apiconfig.model.StationDetails;
import it.pagopa.pagopa.apiconfig.model.Stations;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StationsService {

    @Autowired
    StazioniRepository stazioniRepository;


    @Autowired
    PaStazionePaRepository paStazionePaRepository;

    @Autowired
    ModelMapper modelMapper;

    public Stations getStations(@NotNull Integer limit, @NotNull Integer pageNumber, String brokerCode, String creditorInstitutionCode) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Stazioni> page = stazioniRepository.findAllFilterByIntermediarioAndPa(brokerCode, creditorInstitutionCode, pageable);
        return Stations.builder()
                .stationsList(getStationsList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public StationDetails getStation(@NotNull String stationCode) {
        Optional<Stazioni> result = stazioniRepository.findByIdStazione(stationCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Station not found", "No station found with the provided code");
        }
        Stazioni stazione = result.get();
        return modelMapper.map(stazione, StationDetails.class);
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
}
