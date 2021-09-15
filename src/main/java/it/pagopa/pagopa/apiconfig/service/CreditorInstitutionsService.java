package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditorInstitutionsService {

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CreditorInstitutions getECs() {
        return CreditorInstitutions.builder()
                .creditorInstitutionList(getCreditorInstitutions())
                .build();
    }


    /**
     * Maps all PAs stored in the DB in a List of CreditorInstitution
     * @return a list of {@link CreditorInstitution}.
     */
    private List<CreditorInstitution> getCreditorInstitutions() {
        return paRepository.findAll()
                .stream()
                .map(elem -> modelMapper.map(elem, CreditorInstitution.class))
                .toList();
    }


}
