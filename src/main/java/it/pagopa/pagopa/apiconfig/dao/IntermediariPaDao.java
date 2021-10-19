package it.pagopa.pagopa.apiconfig.dao;

import it.pagopa.pagopa.apiconfig.entity.IntermediariPa;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.repository.IntermediariPaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class IntermediariPaDao {

    @Autowired
    IntermediariPaRepository intermediariPaRepository;


    public IntermediariPa save(IntermediariPa intermediariPa){
        return intermediariPaRepository.save(intermediariPa);
    }


    /**
     * @param brokerCode code of the broker
     * @return search on DB using the {@code brokerCode} and return the IntermediariPa if it is present
     * @throws AppException if not found
     */
    public IntermediariPa getIntermediarioIfExists(String brokerCode) {
        Optional<IntermediariPa> result = intermediariPaRepository.findByIdIntermediarioPa(brokerCode);
        if (result.isEmpty()) {
            throw new AppException(HttpStatus.NOT_FOUND, "Broker not found", "No broker found with the provided code");
        }
        return result.get();
    }


}
