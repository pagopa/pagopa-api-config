package it.gov.pagopa.apiconfig.core.repository;

import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IbanMasterSearchRepository extends IbanRepository {

       @Query("SELECT master FROM IbanMaster master " +
              "JOIN IbanAttributeMaster attribute ON master.objId = attribute.fkIbanMaster " +
              "JOIN IbanAttribute label ON attribute.fkAttribute = label.objId " +
              "JOIN Iban iban ON master.fkIban = iban.objId " +
              "WHERE master.fkPa = ?1 " +
              "AND iban.iban = ?2 " +
              "AND label.attributeName = ?3")
       Page<IbanMaster> findByFkPaAndIbanValueAndLabel(Long fkPa, String ibanValue, String label, Pageable pageable);

       @Query("SELECT master FROM IbanMaster master " +
              "JOIN Iban iban ON master.fkIban = iban.objId " +
              "WHERE master.fkPa = ?1 " +
              "AND iban.iban = ?2")
       Page<IbanMaster> findByFkPaAndIbanValue(Long fkPa, String ibanValue, Pageable pageable);
}
