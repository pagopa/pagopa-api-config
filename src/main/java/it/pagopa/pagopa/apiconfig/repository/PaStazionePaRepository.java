package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PaStazionePaRepository extends PagingAndSortingRepository<PaStazionePa, Long> {

    List<PaStazionePa> findAllByFkPa(Long creditorInstitutionCode);

    Optional<PaStazionePa> findAllByFkPaAndFkStazione_ObjId(Long creditorInstitutionCode, Long stationCode);

    Optional<PaStazionePa> findOne(Specification<PaStazionePa> paStazionePa);

    default Optional<PaStazionePa> findByFkPaAndFkStazione_ObjIdAndAuxDigitAndBroadcastAndSegregazioneAndProgressivo(
            Long creditorInstitutionCode, Long stationCode, Long auxDigit, Boolean broadcast, Long segregationCode, Long applicationCode) {
        return findOne(search(creditorInstitutionCode, stationCode, auxDigit, broadcast, segregationCode, applicationCode));
    }

    static Specification<PaStazionePa> search(Long creditorInstitutionCode, Long stationCode, Long auxDigit, Boolean broadcast, Long segregationCode, Long applicationCode) {
        return (root, cq, cb) -> {
            Predicate ciPred = cb.equal(root.get("fkPa"), creditorInstitutionCode);
            Predicate stationPred = cb.equal(root.get("fkStazione"), stationCode);
            // predicate to find (0 or null), (1), (2), (3 or null)
            Predicate auxR = cb.equal(root.get("auxDigit"), auxDigit);
            Predicate auxDigitPred = auxDigit == 0 || auxDigit == 3 ? cb.or(auxR, cb.isNull(root.get("auxDigit"))) : auxR;
            Predicate broadcastPred = cb.equal(root.get("broadcast"), broadcast);
            Predicate segregationCodePred = segregationCode == null ? cb.isNull(root.get("segregazione")) : cb.equal(root.get("segregazione"), segregationCode);
            Predicate applicationCodePred = applicationCode == null ? cb.isNull(root.get("progressivo")) : cb.equal(root.get("progressivo"), applicationCode);
            return cb.and(ciPred, stationPred, auxDigitPred, broadcastPred, segregationCodePred, applicationCodePred);
        };
    }

}
