package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import it.pagopa.pagopa.apiconfig.model.Iban;
import it.pagopa.pagopa.apiconfig.model.Ibans;
import it.pagopa.pagopa.apiconfig.model.StationCI;
import it.pagopa.pagopa.apiconfig.model.StationCIList;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
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
public class CreditorInstitutionsService {

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private PaStazionePaRepository paStazionePaRepository;

    @Autowired
    private CodifichePaRepository codifichePaRepository;

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
        Optional<Pa> result = paRepository.findByIdDominio(creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Organization not found", "No organization found with the provided fiscal code");
        }
        Pa pa = result.get();
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

    public StationCIList getStationsCI(@NotNull String creditorInstitutionCode) {
        List<PaStazionePa> result = paStazionePaRepository.findAllFilterByPa(creditorInstitutionCode);
        return StationCIList.builder()
                .stationsList(getStationsList(result))
                .build();
    }

    public CreditorInstitutionEncodings getCreditorInstitutionEncodings(String creditorInstitutionCode) {
        List<CodifichePa> encodings = codifichePaRepository.findAllByCodicePa(creditorInstitutionCode);
        return CreditorInstitutionEncodings.builder()
                .encodings(getEncodingList(encodings))
                .build();
    }

    public Ibans getCreditorInstitutionsIbans(String creditorInstitutionCode) {
        List<IbanValidiPerPa> iban = ibanValidiPerPaRepository.findAllByIdDominio(creditorInstitutionCode);
        return Ibans.builder()
                .ibanList(getIbanList(iban))
                .build();
    }


    /**
     * Maps a list of PaStazionePa into a list of StationCI
     *
     * @param paStazionePaList list of {@link PaStazionePa}
     * @return the list of {@link StationCI}
     */
    private List<StationCI> getStationsList(List<PaStazionePa> paStazionePaList) {
        return paStazionePaList.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, StationCI.class))
                .collect(Collectors.toList());
    }

    /**
     * Maps all PAs in a page stored in the DB in a List of CreditorInstitution
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
     * @param encodings list of {@link CodifichePa}
     * @return maps into a list of {@link Encoding}
     */
    private List<Encoding> getEncodingList(List<CodifichePa> encodings) {
        return encodings.stream()
                .filter(Objects::nonNull)
                .filter(elem -> elem.getFkCodifica() != null)
                .map(elem -> Encoding.builder()
                        .codeType(Encoding.CodeTypeEnum.fromValue(elem.getFkCodifica().getIdCodifica()))
                        .build())
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
