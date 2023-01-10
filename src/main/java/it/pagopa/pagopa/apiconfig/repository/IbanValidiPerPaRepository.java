package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IbanValidiPerPaRepository extends JpaRepository<IbanValidiPerPa, String> {

    List<IbanValidiPerPa> findAllByFkPa(Long fkPa);
    List<IbanValidiPerPa> findAllByIbanAccreditoContainsIgnoreCase(String iban);
}
