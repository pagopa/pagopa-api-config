package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Psp;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PspRepository extends PagingAndSortingRepository<Psp, Long> {

    Optional<Psp> findByIdPsp(String id);

}
