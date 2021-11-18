package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IcaService {

    @Autowired
    InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

    @Autowired
    ModelMapper modelMapper;

    public Icas getIcas(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<InformativeContoAccreditoMaster> page = informativeContoAccreditoMasterRepository.findAll(pageable);
        return Icas.builder()
                .icaList(getIcaList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public byte[] getIca(@NotNull String idIca, String creditorInstitutionCode) {
        Optional<InformativeContoAccreditoMaster> result = informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(idIca, creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(AppError.ICA_NOT_FOUND, idIca);
        }
        return result.get().getFkBinaryFile().getFileContent();
    }


    /**
     * Maps InformativeContoAccreditoMaster objects stored in the DB in a List of Ica
     *
     * @param page page of {@link InformativeContoAccreditoMaster} returned from the database
     * @return a list of {@link Ica}.
     */
    private List<Ica> getIcaList(Page<InformativeContoAccreditoMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Ica.class))
                .collect(Collectors.toList());
    }
}
