package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StazioniRepository extends JpaRepository<Stazioni, Long> {

    Optional<Stazioni> findByIdStazione(String stationCode);

    @Query(value = "select distinct s from PaStazionePa r, Stazioni s " +
            "where (:fkIntermediario is null or  s.fkIntermediarioPa = :fkIntermediario) " +
            "and (:fkPa is null or (r.fkPa = :fkPa))")
    Page<Stazioni> findAllFilterByIntermediarioAndPa(@Param("fkIntermediario") Long fkIntermediario, @Param("fkPa") Long fkPa, Pageable pageable);

}
