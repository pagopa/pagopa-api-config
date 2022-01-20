package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfigurationKeysRepository extends JpaRepository<ConfigurationKeys, ConfigurationKeys> {
}
