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

    @Query("select distinct s from PaStazionePa r join r.fkPa p join r.fkStazione s join s.fkIntermediarioPa i " +
            "where (:brokerCode is null or i.idIntermediarioPa = :brokerCode) and (:creditorInstitutionCode is null or p.idDominio = :creditorInstitutionCode)")
    Page<Stazioni> findAllFilterByIntermediarioAndPa(@Param("brokerCode") String brokerCode, @Param("creditorInstitutionCode") String creditorInstitutionCode, Pageable pageable);

}
