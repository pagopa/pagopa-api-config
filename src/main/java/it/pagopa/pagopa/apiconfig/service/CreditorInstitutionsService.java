package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.*;
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
    private ModelMapper modelMapper;


    public CreditorInstitutions getECs(Integer limit, Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Pa> page = paRepository.findAll(pageable);
        return CreditorInstitutions.builder()
                .creditorInstitutionList(getCreditorInstitutions(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public CreditorInstitutionDetails getEC(String organizationFiscalCode) {
        Optional<Pa> result = paRepository.findByIdDominio(organizationFiscalCode);
        if (result.isPresent()) {
            Pa pa = result.get();
            List<Station> stations = getStationList(pa);
            CreditorInstitutionFull creditorInstitutionFull = modelMapper.map(pa, CreditorInstitutionFull.class);
            return CreditorInstitutionDetails.builder()
                    .creditorInstitutionFull(creditorInstitutionFull)
                    .stations(stations)
                    .build();
        } else {
            throw new AppException(HttpStatus.NOT_FOUND, "Organization not found", "No organization found with the provided fiscal code", null);
        }
    }


    /**
     * @param pa CE
     * @return the list of the stations of a PA
     */
    private List<Station> getStationList(Pa pa) {
        List<PaStazionePa> relations = paStazionePaRepository.findAllByFkPa(pa.getObjId());
        return relations.stream()
                .filter(Objects::nonNull)
                .map(PaStazionePa::getFkStazioni)
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, Station.class))
                .collect(Collectors.toList());
    }


    /**
     * Maps all PAs stored in the DB in a List of CreditorInstitution
     *
     * @param page page of PA returned from the database
     * @return a list of {@link CreditorInstitutionFull}.
     */
    private List<CreditorInstitutionLight> getCreditorInstitutions(Page<Pa> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, CreditorInstitutionLight.class))
                .collect(Collectors.toList());
    }

}
