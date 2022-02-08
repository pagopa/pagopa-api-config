package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.InformativePaMaster;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface InformativePaMasterRepository extends PagingAndSortingRepository<InformativePaMaster, Long> {

    Optional<InformativePaMaster> findByIdInformativaPaAndFkPa_IdDominio(String idCounterpartTable, String creditorInstitutionCode);

    List<InformativePaMaster> findByFkPa_IdDominioAndDataInizioValiditaLessThanOrderByDataInizioValiditaDesc(String idDominio, Timestamp now);

}
