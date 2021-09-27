package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.Stazioni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StazioniRepository extends PagingAndSortingRepository<Stazioni, Long> {

    Optional<Stazioni> findByIdStazione(String stationCode);

    @Query(value = "select distinct NODO4_CFG.STAZIONI.* " +
            "from NODO4_CFG.STAZIONI, NODO4_CFG.PA_STAZIONE_PA, NODO4_CFG.PA, NODO4_CFG.INTERMEDIARI_PA " +
            // filter conditions
            "where (?1 is null or NODO4_CFG.INTERMEDIARI_PA.ID_INTERMEDIARIO_PA=?1) " +
            "and (?2 is null or NODO4_CFG.PA.ID_DOMINIO=?2) " +
            // join conditions
            "and NODO4_CFG.PA_STAZIONE_PA.FK_PA=NODO4_CFG.PA.OBJ_ID and NODO4_CFG.PA_STAZIONE_PA.FK_STAZIONE=NODO4_CFG.STAZIONI.OBJ_ID and NODO4_CFG.INTERMEDIARI_PA.OBJ_ID=NODO4_CFG.STAZIONI.FK_INTERMEDIARIO_PA",
            nativeQuery = true)
    Page<Stazioni> findAllFilterByIntermediarioAndPa(String intermediaryCode, String creditorInstitutionCode, Pageable pageable);

}
