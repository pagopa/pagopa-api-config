package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings(
    "java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CodifichePaRepository extends JpaRepository<CodifichePa, Long> {

  List<CodifichePa> findAllByFkPa_ObjId(Long fkPa);

  List<CodifichePa> findAllByCodicePaAndFkCodifica_IdCodifica(String codicePa, String idCodifica);

  Optional<CodifichePa> findByCodicePa(String codicePa);

  Optional<CodifichePa> findByCodicePaAndFkPa_ObjId(String codicePa, Long fkPa);
}
