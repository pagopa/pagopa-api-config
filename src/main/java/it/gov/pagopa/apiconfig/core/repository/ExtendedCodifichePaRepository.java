package it.gov.pagopa.apiconfig.core.repository;

import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtendedCodifichePaRepository extends CodifichePaRepository {

    @Modifying
    @Query("delete from CodifichePa cp where cp.id in ?1")
    void deleteByIds(List<Long> ids);
}
