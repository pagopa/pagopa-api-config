package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import it.pagopa.pagopa.apiconfig.repository.CodifichePaRepository;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EncodingsService {

    @Autowired
    private PaRepository paRepository;

    @Autowired
    private CodifichePaRepository codifichePaRepository;

    @Autowired
    private ModelMapper modelMapper;


    public CreditorInstitutionEncodings getCreditorInstitutionEncodings(@NotNull String creditorInstitutionCode) {
        Pa pa = paRepository.findByIdDominio(creditorInstitutionCode)
                .orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTION_NOT_FOUND, creditorInstitutionCode));
        List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
        return CreditorInstitutionEncodings.builder()
                .encodings(getEncodingList(encodings))
                .build();
    }


    /**
     * @param encodings list of {@link CodifichePa}
     * @return maps into a list of {@link Encoding}
     */
    private List<Encoding> getEncodingList(List<CodifichePa> encodings) {
        return encodings.stream()
                .filter(Objects::nonNull)
                .filter(elem -> elem.getFkCodifica() != null)
                .map(elem -> modelMapper.map(elem, Encoding.class))
                .collect(Collectors.toList());
    }


}
