package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Station;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaStazionePaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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


    public CreditorInstitutions getECs() {
        return CreditorInstitutions.builder()
                .creditorInstitutionList(getCreditorInstitutions())
                .build();
    }

    public CreditorInstitutionDetails getEC(String organizationFiscalCode) {
        Optional<Pa> result = paRepository.findByIdDominio(organizationFiscalCode);
        if (result.isPresent()) {
            Pa pa = result.get();
            List<Station> stations = getStationList(pa);
            CreditorInstitution creditorInstitution = modelMapper.map(pa, CreditorInstitution.class);
            return CreditorInstitutionDetails.builder()
                    .creditorInstitution(creditorInstitution)
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
     * @return a list of {@link CreditorInstitution}.
     */
    private List<CreditorInstitution> getCreditorInstitutions() {
        return paRepository.findAll()
                .stream()
                .map(elem -> modelMapper.map(elem, CreditorInstitution.class))
                .collect(Collectors.toList());
    }

}
