package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Cache;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CacheRepository extends PagingAndSortingRepository<Cache, Long> {

    Optional<Cache> findFirstByVersion(String version, Sort by);

    List<Cache> findAll();
}
