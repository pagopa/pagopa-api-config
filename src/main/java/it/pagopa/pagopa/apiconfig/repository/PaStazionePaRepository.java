package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaStazionePaRepository extends PagingAndSortingRepository<PaStazionePa, Long> {


    @Query(value = "select distinct r from PaStazionePa r join r.fkPa p join r.fkStazione s " +
            "where (:creditorInstitutionCode is null or p.idDominio=:creditorInstitutionCode) ")
    List<PaStazionePa> findAllFilterByPa(@Param("creditorInstitutionCode") String creditorInstitutionCode);

}
