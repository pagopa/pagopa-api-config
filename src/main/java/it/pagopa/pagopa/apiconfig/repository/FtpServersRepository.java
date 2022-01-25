package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.FtpServers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FtpServersRepository extends JpaRepository<FtpServers, Long> {
    Optional<FtpServers> findByHostAndPortAndService(String host, Integer port, String service);
}
