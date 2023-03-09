package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Pdd;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PddRepository extends JpaRepository<Pdd, Long> {
  Optional<Pdd> findByIdPdd(String idPdd);
}
