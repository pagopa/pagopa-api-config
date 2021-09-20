package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaStazionePaRepository extends JpaRepository<PaStazionePa, Long> {

    List<PaStazionePa> findAllByFkPa(Long fkPa);
}