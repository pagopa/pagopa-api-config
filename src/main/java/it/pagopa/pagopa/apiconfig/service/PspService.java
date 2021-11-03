package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProvider;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviderDetails;
import it.pagopa.pagopa.apiconfig.model.psp.PaymentServiceProviders;
import it.pagopa.pagopa.apiconfig.repository.PspRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PspService {
    @Autowired
    PspRepository pspRepository;

    @Autowired
    ModelMapper modelMapper;

    public PaymentServiceProviders getPaymentServiceProviders(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<Psp> page = pspRepository.findAll(pageable);
        return PaymentServiceProviders.builder()
                .paymentServiceProviderList(getPaymentServiceProviderList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }

    public PaymentServiceProviderDetails getPaymentServiceProvider(@NotNull String pspCode) {
        Psp psp = pspRepository.findByIdPsp(pspCode)
                .orElseThrow(() -> new AppException(AppError.PSP_NOT_FOUND, pspCode));
        return modelMapper.map(psp, PaymentServiceProviderDetails.class);
    }

    /**
     * Maps PSP objects stored in the DB in a List of PaymentServiceProvider
     *
     * @param page page of PSP returned from the database
     * @return a list of {@link PaymentServiceProvider}.
     */
    private List<PaymentServiceProvider> getPaymentServiceProviderList(Page<Psp> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, PaymentServiceProvider.class))
                .collect(Collectors.toList());
    }
}
