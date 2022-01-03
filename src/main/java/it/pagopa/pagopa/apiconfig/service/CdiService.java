package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.CdiMaster;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.Cdi;
import it.pagopa.pagopa.apiconfig.model.psp.Cdis;
import it.pagopa.pagopa.apiconfig.repository.CdiMasterRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CdiService {

    @Autowired
    private CdiMasterRepository cdiMasterRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Cdis getCdis(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<CdiMaster> page = cdiMasterRepository.findAll(pageable);
        return Cdis.builder()
                .cdiList(getCdiList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public byte[] getCdi(@NotBlank String idCdi, @NotBlank String pspCode) {
        CdiMaster cdiMaster = cdiMasterRepository.findByIdInformativaPspAndFkPsp_IdPsp(idCdi, pspCode)
                .orElseThrow(() -> new AppException(AppError.CDI_NOT_FOUND, idCdi));

        return cdiMaster.getFkBinaryFile().getFileContent();
    }

    /**
     * Maps CdiMaster objects stored in the DB in a List of Cdi
     *
     * @param page page of {@link CdiMaster} returned from the database
     * @return a list of {@link Cdi}.
     */
    private List<Cdi> getCdiList(Page<CdiMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Cdi.class))
                .collect(Collectors.toList());
    }

}
