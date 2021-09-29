package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.IbanValidiPerPa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IbanValidiPerPaRepository extends JpaRepository<IbanValidiPerPa, String> {

    @Query("select i from IbanValidiPerPa i, Pa p where p.idDominio=:creditorInstitutionCode and p.objId=i.fkPa")
    List<IbanValidiPerPa> findAllByIdDominio(String creditorInstitutionCode);
}
