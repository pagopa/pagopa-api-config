package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.models.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.models.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import it.pagopa.pagopa.apiconfig.util.CreditorInstitutionsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditorInstitutionsService {

    @Autowired
    PaRepository paRepository;

    public CreditorInstitutions getECs() {
        List<CreditorInstitution> creditorInstitutions = paRepository.findAll()
                .stream()
                .map(CreditorInstitutionsMapper::mapPaToCreditorInstitution)
                .toList();
        return CreditorInstitutions.builder()
                .creditorInstitutionList(creditorInstitutions)
                .build();
    }

}
