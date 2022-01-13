package it.pagopa.pagopa.apiconfig.repository;

import it.pagopa.pagopa.apiconfig.entity.WfespPluginConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WfespPluginConfRepository extends JpaRepository<WfespPluginConf, Long> {

    Optional<WfespPluginConf> findByIdServPlugin(String id);
}
