package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.ConfigurationKeyPK;
import it.pagopa.pagopa.apiconfig.entity.ConfigurationKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigurationKeysRepository extends JpaRepository<ConfigurationKeys, ConfigurationKeyPK> {

    @Query(value = "select new ConfigurationKeys(ck.configCategory, ck.configDescription, ck.configKey, ck.configValue) from ConfigurationKeys ck")
    List<ConfigurationKeys> findAll();
    Optional<ConfigurationKeys> findByConfigKey(String key);
}
