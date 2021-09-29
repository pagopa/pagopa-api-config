package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.CodifichePa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodifichePaRepository extends JpaRepository<CodifichePa, Long> {

    List<CodifichePa> findAllByFkPa_ObjId(Long fkPa);
}
