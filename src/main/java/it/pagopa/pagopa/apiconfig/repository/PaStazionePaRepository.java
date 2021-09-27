package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaStazionePaRepository extends PagingAndSortingRepository<PaStazionePa, Long> {

    @Query(value = "select distinct NODO4_CFG.PA_STAZIONE_PA.* " +
            "from NODO4_CFG.STAZIONI, NODO4_CFG.PA_STAZIONE_PA, NODO4_CFG.PA " +
            // filter conditions
            "where (?1 is null or NODO4_CFG.PA.ID_DOMINIO=?1) " +
            // join conditions
            "and NODO4_CFG.PA_STAZIONE_PA.FK_PA=NODO4_CFG.PA.OBJ_ID and NODO4_CFG.PA_STAZIONE_PA.FK_STAZIONE=NODO4_CFG.STAZIONI.OBJ_ID",
            nativeQuery = true)
    List<PaStazionePa> findAllFilterByPa(String creditorInstitutionCode);


}
