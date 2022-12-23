package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Station;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationCreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationCreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.StationDetails;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Stations;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.pagopa.pagopa.apiconfig.repository.StazioniRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.pagopa.pagopa.apiconfig.util.CommonUtil.deNull;

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

    @Value("${properties.environment}")
    private String env;


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

    public byte[] getStationCreditorInstitutionsCSV(String stationCode) {
        Stazioni stazioni = getStationIfExists(stationCode);
        List<PaStazionePa> queryResult = paStazioniRepository.findAllByFkStazione_ObjId(stazioni.getObjId());

        var csvRows = queryResult.stream()
                .map(paStazionePa -> modelMapper.map(paStazionePa, StationCreditorInstitution.class))
                .map(this::getCsvValues)
                .collect(Collectors.toList());
        List<String> headers = Arrays.asList("Nome",
                "Codice",
                "Abilitato",
                "Aux digit",
                "ApplicationCode",
                "Codice Segregazione",
                "Broadcast");
        return CommonUtil.createCsv(headers, csvRows);
    }
    
    public StationCreditorInstitution getStationCreditorInstitutionRelation(@NotNull String stationCode, @NotNull String creditorInstitutionCode) {
    	// verify creditor institution
        Pa pa = getCreditorInstitutionIfExist(creditorInstitutionCode);
        // verify station
        Stazioni stazioni = getStationIfExists(stationCode);
        // verify relation
        PaStazionePa relation = getRelationCreditorInstitutionStationIfExist(stazioni, pa);
        
        return modelMapper.map(relation, StationCreditorInstitution.class);
    }

    public byte[] getStationsCSV() {
        List<Stazioni> stations = stazioniRepository.findAll();
        var csvRows = stations.stream()
                .map(this::getCsvValuesWithUrl)
                .collect(Collectors.toList());
        List<String> headers = Arrays.asList("Descrizione Intermediario EC",
                "Stazione",
                "Abilitato",
                "Versione",
                "URL");
        return CommonUtil.createCsv(headers, csvRows);
    }

    private List<String> getCsvValues(StationCreditorInstitution elem) {
        List<String> list = new ArrayList<>();
        list.add(deNull(elem.getBusinessName()));
        list.add(deNull(elem.getCreditorInstitutionCode()));
        list.add(deNull(elem.getEnabled()).toString());
        list.add(deNull(elem.getAuxDigit()));
        list.add(deNull(elem.getApplicationCode()));
        list.add(deNull(elem.getSegregationCode()));
        list.add(deNull(elem.getBroadcast()).toString());
        return list;
    }

    private List<String> getCsvValues(Stazioni station) {
        List<String> list = new ArrayList<>();
        list.add(deNull(station.getIntermediarioPa().getCodiceIntermediario()));
        list.add(deNull(station.getIdStazione()));
        list.add(deNull(station.getEnabled()).toString());
        list.add(deNull(station.getVersione()));
        return list;
    }

    private List<String> getCsvValuesWithUrl(Stazioni station) {
        List<String> list = getCsvValues(station);
        list.add(String.format("https://config%s.platform.pagopa.it/stations/%s", CommonUtil.getEnvironment(env), deNull(station.getIdStazione())));
        return list;
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
     * @param creditorInstitutionCode the code of creditor institution to check
     * @return search on DB using the {@code stationCode} and return the creditor institution if it is present
     */
    private Pa getCreditorInstitutionIfExist(String creditorInstitutionCode) {
        Optional<Pa> result = paRepository.findByIdDominio(creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode);
        }
        return result.get();
    }
    
    /**
     * @param stazioni the station object
     * @param pa the creditor institution object
     * @return search on DB and return the relation if it is present
     */
    private PaStazionePa getRelationCreditorInstitutionStationIfExist(Stazioni stazioni, Pa pa) {
        Optional<PaStazionePa> result = paStazioniRepository.findAllByFkPaAndFkStazione_ObjId(pa.getObjId(), stazioni.getObjId());
        if (result.isEmpty()) {
            throw new AppException(AppError.RELATION_STATION_NOT_FOUND, pa.getIdDominio() , stazioni.getIdStazione());
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
