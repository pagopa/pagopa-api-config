package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Codifiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodificheRepository extends JpaRepository<Codifiche, Long> {

    Codifiche findByIdCodifica(String codeType);
}
