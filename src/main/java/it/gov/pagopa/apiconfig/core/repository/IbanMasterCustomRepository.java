package it.gov.pagopa.apiconfig.core.repository;

import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IbanMasterCustomRepository extends JpaRepository<IbanMaster, Long> {

       @Query("select master from IbanMaster master, IbanAttributeMaster attribute, IbanAttribute label " +
       "where master.objId = attribute.fkIbanMaster " +
       "and attribute.fkAttribute = label.objId " +
       "and master.fkPa = ?1 " +
       "and master.fkIban = ?2 " +
       "and label.attributeName = ?3")
       Page<IbanMaster> findByFkPaAndFkIbanAndLabel(Long fkPa, Long fkIban, String label, Pageable pageable);

       @Query("select master from IbanMaster master " +
       "where master.fkPa = ?1 " +
       "and master.fkIban = ?2")
       Page<IbanMaster> findByFkPaAndFkIban(Long fkPa, Long fkIban, Pageable pageable);
}
