package it.pagopa.pagopa.apiconfig.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import it.pagopa.pagopa.apiconfig.entity.CdiCosmos;
import org.springframework.stereotype.Repository;

@Repository
public interface CDIRepository extends CosmosRepository<CdiCosmos, String> {
}
