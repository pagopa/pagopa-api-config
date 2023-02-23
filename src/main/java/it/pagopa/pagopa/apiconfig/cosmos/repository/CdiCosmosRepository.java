package it.pagopa.pagopa.apiconfig.cosmos.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import it.pagopa.pagopa.apiconfig.cosmos.container.CdiCosmos;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiCosmosRepository extends CosmosRepository<CdiCosmos, String> {}
