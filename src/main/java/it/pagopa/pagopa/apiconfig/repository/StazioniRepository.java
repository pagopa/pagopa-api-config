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
//    select distinct s.*
//    from STAZIONI s, PA_STAZIONE_PA r, INTERMEDIARI_PA i, PA p
//    where s.FK_INTERMEDIARIO_PA = i.OBJ_ID
//    and (?1 is null or i.ID_INTERMEDIARIO_PA = ?1)
//    and (?2 is null or (p.ID_DOMINIO = ?2 and r.FK_PA = p.OBJ_ID and r.FK_STAZIONE = s.OBJ_ID))
    Page<Stazioni> findAllFilterByIntermediarioAndPa(@Param("brokerCode") String brokerCode, @Param("creditorInstitutionCode") String creditorInstitutionCode, Pageable pageable);

}
