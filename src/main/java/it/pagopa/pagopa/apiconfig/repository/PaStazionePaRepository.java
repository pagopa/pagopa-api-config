package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PaStazionePaRepository extends PagingAndSortingRepository<PaStazionePa, Long> {

    List<PaStazionePa> findAllByFkPa_ObjId(Long creditorInstitutionCode);

    Optional<PaStazionePa> findAllByFkPa_ObjIdAndFkStazione_ObjId(Long creditorInstitutionCode, Long stationCode);

}
