package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StazioniRepository extends PagingAndSortingRepository<Stazioni, Long> {

    Optional<Stazioni> findByIdStazione(String stationCode);

    @Query(value = "select distinct s from Stazioni s, PaStazionePa r, IntermediariPa i, Pa p " +
            "where s.fkIntermediarioPa = i " +
            "and (:brokerCode is null or i.idIntermediarioPa = :brokerCode) " +
            "and (:creditorInstitutionCode is null or (p.idDominio = :creditorInstitutionCode and r.fkPa = p and r.fkStazione = s))")
    Page<Stazioni> findAllFilterByIntermediarioAndPa(@Param("brokerCode") String brokerCode, @Param("creditorInstitutionCode") String creditorInstitutionCode, Pageable pageable);

}
