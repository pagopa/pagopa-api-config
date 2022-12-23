package it.pagopa.pagopa.apiconfig.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import it.pagopa.pagopa.apiconfig.entity.PaymentTypesCosmos;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTypesCosmosRepository extends CosmosRepository<PaymentTypesCosmos, String> {
    void deleteByName(String paymentTypeName);
}
