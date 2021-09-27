package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaStazionePaRepository extends PagingAndSortingRepository<PaStazionePa, Long> {

    List<PaStazionePa> findAllByFkPa(Long fkPa);

    Page<PaStazionePa> findAllByPa_IdDominio(String creditorInstitutionCode, Pageable pageable);
//    Page<PaStazionePa> findDistinctByPa_IdDominio(String creditorInstitutionCode, Pageable pageable);

}
