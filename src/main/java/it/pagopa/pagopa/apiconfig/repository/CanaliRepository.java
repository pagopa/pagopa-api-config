package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Canali;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CanaliRepository extends JpaRepository<Canali, Long> {

    Optional<Canali> findByIdCanale(String idCanale);
}
