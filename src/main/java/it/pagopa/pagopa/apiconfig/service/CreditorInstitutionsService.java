package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitution;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionDetails;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionEncodings;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionStation;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutionStationList;
import it.pagopa.pagopa.apiconfig.model.CreditorInstitutions;
import it.pagopa.pagopa.apiconfig.model.Encoding;
import it.pagopa.pagopa.apiconfig.model.Iban;
import it.pagopa.pagopa.apiconfig.model.Ibans;
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
import javax.validation.constraints.Size;
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
        Pa pa = getPaIfExists(creditorInstitutionCode);
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

    public CreditorInstitutionDetails createCreditorInstitution(@NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        if (paRepository.findByIdDominio(creditorInstitutionDetails.getCreditorInstitutionCode()).isPresent()) {
            throw new AppException(HttpStatus.CONFLICT, "Conflict: integrity violation", "creditor_institution_code already presents");
        }
        Pa pa = paRepository.save(modelMapper.map(creditorInstitutionDetails, Pa.class));
        return modelMapper.map(pa, CreditorInstitutionDetails.class);
    }

    public CreditorInstitutionDetails updateCreditorInstitution(@Size(max = 50) String creditorInstitutionCode, @NotNull CreditorInstitutionDetails creditorInstitutionDetails) {
        Long objId = getPaIfExists(creditorInstitutionCode).getObjId();
        Pa pa = modelMapper.map(creditorInstitutionDetails, Pa.class).toBuilder()
                .objId(objId)
                .build();
        Pa result = paRepository.save(pa);
        return modelMapper.map(result, CreditorInstitutionDetails.class);
    }

    public void deleteCreditorInstitution(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        paRepository.delete(pa);
    }

    public CreditorInstitutionStationList getCreditorInstitutionStations(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<PaStazionePa> result = paStazionePaRepository.findAllByFkPa_ObjId(pa.getObjId());
        return CreditorInstitutionStationList.builder()
                .stationsList(getStationsList(result))
                .build();
    }

    public CreditorInstitutionEncodings getCreditorInstitutionEncodings(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<CodifichePa> encodings = codifichePaRepository.findAllByFkPa_ObjId(pa.getObjId());
        return CreditorInstitutionEncodings.builder()
                .encodings(getEncodingList(encodings))
                .build();
    }

    public Ibans getCreditorInstitutionsIbans(@NotNull String creditorInstitutionCode) {
        Pa pa = getPaIfExists(creditorInstitutionCode);
        List<IbanValidiPerPa> iban = ibanValidiPerPaRepository.findAllByFkPa(pa.getObjId());
        return Ibans.builder()
                .ibanList(getIbanList(iban))
                .build();
    }


    /**
     * @param creditorInstitutionCode idDominio
     * @return return the PA record from DB if Exists
     * @throws AppException if not found
     */
    private Pa getPaIfExists(String creditorInstitutionCode) throws AppException {
        Optional<Pa> result = paRepository.findByIdDominio(creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Creditor Institution not found", "No creditor institution found with the provided code");
        }
        return result.get();
    }


    /**
     * Maps a list of PaStazionePa into a list of CreditorInstitutionStation
     *
     * @param paStazionePaList list of {@link PaStazionePa}
     * @return the list of {@link CreditorInstitutionStation}
     */
    private List<CreditorInstitutionStation> getStationsList(List<PaStazionePa> paStazionePaList) {
        return paStazionePaList.stream()
                .filter(Objects::nonNull)
                .map(elem -> modelMapper.map(elem, CreditorInstitutionStation.class))
                .collect(Collectors.toList());
    }

    /**
     * Maps PA objects stored in the DB in a List of CreditorInstitution
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
                .map(elem -> modelMapper.map(elem, Encoding.class))
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
